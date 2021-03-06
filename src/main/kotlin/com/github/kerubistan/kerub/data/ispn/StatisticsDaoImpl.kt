package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.ControllerConfigDao
import com.github.kerubistan.kerub.data.stat.BasicBalanceReport
import com.github.kerubistan.kerub.data.stat.StatisticsDao
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.expectations.CoreDedicationExpectation
import com.github.kerubistan.kerub.utils.equalsAnyOf
import io.github.kerubistan.kroki.numbers.sumBy
import nl.komponents.kovenant.task
import org.infinispan.Cache
import java.math.BigInteger
import java.util.UUID

class StatisticsDaoImpl(
		private val hostCache: Cache<UUID, Host>,
		private val hostDynamicCache: Cache<UUID, HostDynamic>,
		private val vmCache: Cache<UUID, VirtualMachine>,
		private val vdiskCache: Cache<UUID, VirtualStorageDevice>,
		private val vdiskDynDao: Cache<UUID, VirtualStorageDeviceDynamic>,
		private val configDao: ControllerConfigDao
) : StatisticsDao {

	companion object {
		val bigIntSum = { first: BigInteger, second: BigInteger -> first + second }
		val intSum = { first: Int, second: Int -> first + second }
	}

	override fun basicBalanceReport(): BasicBalanceReport {

		val config = configDao.get()

		val totalMemory = task {
			hostCache.parallelStream().map { it.value.capabilities?.totalMemory ?: BigInteger.ZERO }
					.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		val totalHosts = task {
			hostCache.parallelStream().map { 1 }
					.reduce(intSum).orElse(0)
		}

		val hostsOnline = task {
			hostDynamicCache.parallelStream().map {
				if (it.value.status == HostStatus.Up) {
					1
				} else {
					0
				}
			}
					.reduce(intSum).orElse(0)
		}

		val totalVms = task {
			vmCache.parallelStream().map { 1 }
					.reduce(intSum).orElse(0)
		}

		val totalVmMinMemory = task {
			vmCache.parallelStream().map { it.value.memory.min }
					.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		val totalVmMaxMemory = task {
			vmCache.parallelStream().map { it.value.memory.max }
					.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		val totalHostCpus = task {
			hostCache.parallelStream().map { it.value.capabilities?.cpus?.size ?: 0 }
					.reduce(intSum).orElse(0)
		}

		val totalVmCpus = task {
			vmCache.parallelStream().map { it.value.nrOfCpus }
					.reduce(intSum).orElse(0)
		}

		val totalDedicatedVmCpus = task {
			vmCache.parallelStream()
					.filter {
						it.value.expectations.any {
							it is CoreDedicationExpectation && it.level.equalsAnyOf(ExpectationLevel.DealBreaker, ExpectationLevel.Want)
						}
					}
					.map { it.value.nrOfCpus }
					.reduce(intSum).orElse(0)
		}

		val totalHostStorage = task {
			hostCache.parallelStream().map {
				config.storageTechnologies.enabledCapabilities(it.value.capabilities?.storageCapabilities ?: listOf())
						.sumBy(StorageCapability::size)
			}.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		val freeHostStorage = task {
			val storageConfig = config.storageTechnologies
			hostCache.parallelStream().parallel().map {
				it.value to hostDynamicCache.getAsync(it.value.id)
			}.map {
				val dyn = it.second.get()
				val stat = it.first
				stat.capabilities?.storageCapabilities?.filter(storageConfig::isEnabled)?.mapNotNull { cap ->
					dyn?.storageStatus?.firstOrNull { cap.id == it.id }
				}?.sumBy { dynamic -> dynamic.freeCapacity } ?: BigInteger.ZERO
			}.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		val totalDiskStorageRequested = task {
			vdiskCache.parallelStream().map {
				it.value.size
			}.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		val totalDiskStorageActual = task {
			vdiskDynDao.parallelStream().map {
				it.value.allocations.sumBy { it.actualSize }
			}.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		return BasicBalanceReport(
				totalHosts = totalHosts.get(),
				hostsOnline = hostsOnline.get(),
				totalVms = totalVms.get(),
				totalHostMemory = totalMemory.get(),
				totalMinVmMemory = totalVmMinMemory.get(),
				totalMaxVmMemory = totalVmMaxMemory.get(),
				totalHostCpus = totalHostCpus.get(),
				totalVmCpus = totalVmCpus.get(),
				totalDedicatedVmCpus = totalDedicatedVmCpus.get(),
				totalHostStorage = totalHostStorage.get(),
				totalHostStorageFree = freeHostStorage.get(),
				totalDiskStorageRequested = totalDiskStorageRequested.get(),
				totalDiskStorageActual = totalDiskStorageActual.get()
		)
	}
}