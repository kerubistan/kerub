package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.stat.BasicBalanceReport
import com.github.K0zka.kerub.data.stat.StatisticsDao
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.expectations.CoreDedicationExpectation
import com.github.K0zka.kerub.utils.equalsAnyOf
import com.github.K0zka.kerub.utils.sumBy
import nl.komponents.kovenant.task
import org.infinispan.Cache
import java.math.BigInteger
import java.util.UUID

class StatisticsDaoImpl(
		private val hostCache: Cache<UUID, Host>,
		private val vmCache: Cache<UUID, VirtualMachine>,
		private val vdiskCache: Cache<UUID, VirtualStorageDevice>,
		private val vdiskDynDao: Cache<UUID, VirtualStorageDeviceDynamic>
) : StatisticsDao {

	companion object {
		val bigIntSum = { first: BigInteger, second: BigInteger -> first + second }
		val intSum = { first: Int, second: Int -> first + second }
	}

	override fun basicBalanceReport(): BasicBalanceReport {

		val totalMemory = task {
			hostCache.parallelStream().map { it.value.capabilities?.totalMemory ?: BigInteger.ZERO }
					.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		val totalHosts = task {
			hostCache.parallelStream().map { 1 }
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
				it.value.capabilities?.storageCapabilities?.sumBy { it.size } ?: BigInteger.ZERO
			}.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		val totalDiskStorageRequested = task {
			vdiskCache.parallelStream().map {
				it.value.size
			}.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		val totalDiskStorageActual = task {
			vdiskDynDao.parallelStream().map {
				it.value.actualSize
			}.reduce(bigIntSum).orElse(BigInteger.ZERO)
		}

		return BasicBalanceReport(
				totalHosts = totalHosts.get(),
				totalVms = totalVms.get(),
				totalHostMemory = totalMemory.get(),
				totalMinVmMemory = totalVmMinMemory.get(),
				totalMaxVmMemory = totalVmMaxMemory.get(),
				totalHostCpus = totalHostCpus.get(),
				totalVmCpus = totalVmCpus.get(),
				totalDedicatedVmCpus = totalDedicatedVmCpus.get(),
				totalHostStorage = totalHostStorage.get(),
				totalDiskStorageRequested = totalDiskStorageRequested.get(),
				totalDiskStorageActual = totalDiskStorageActual.get()
		)
	}
}