package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.GvinumStorageCapability
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.K0zka.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.K0zka.kerub.model.expectations.CoreDedicationExpectation
import com.github.K0zka.kerub.testCpu
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testHostCapabilities
import com.github.K0zka.kerub.testVirtualDisk
import com.github.K0zka.kerub.testVm
import com.github.K0zka.kerub.utils.toSize
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class StatisticsDaoImplTest {

	protected var cacheManager: DefaultCacheManager? = null
	protected var vmCache: Cache<UUID, VirtualMachine>? = null
	protected var hostCache: Cache<UUID, Host>? = null
	protected var vdiskCache: Cache<UUID, VirtualStorageDevice>? = null
	protected var vdiskDynCache: Cache<UUID, VirtualStorageDeviceDynamic>? = null

	@Before
	fun setUp() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		vmCache = getCleanCache("vmCache")
		hostCache = getCleanCache("hostCache")
		vdiskCache = getCleanCache("vdiskCache")
		vdiskDynCache = getCleanCache("vdiskDynCache")
	}

	private fun <K, V> getCleanCache(cacheName: String): Cache<K, V> {
		val cache: Cache<K, V> = cacheManager!!.getCache(cacheName)
		cache.clear()
		return cache
	}

	@After
	fun cleanup() {
		vmCache?.clear()
		hostCache?.clear()
		vdiskCache?.clear()
		cacheManager!!.stop()
	}

	val host1 = testHost.copy(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			capabilities = testHostCapabilities.copy(
					cpus = listOf(testCpu, testCpu),
					totalMemory = "256 GB".toSize(),
					storageCapabilities = listOf(
							LvmStorageCapability(
									id = UUID.randomUUID(),
									physicalVolumes = listOf("128 GB".toSize(), "128 GB".toSize()),
									size = "256 GB".toSize(),
									volumeGroupName = "data-vg"
							)
					)
			)
	)

	val gvinumDisk = GvinumStorageCapability(
			id = UUID.randomUUID(),
			size = "256 GB".toSize(),
			name = "gvinum-disk-1",
			device = "/dev/blah"
	)
	val host2 = testHost.copy(
			id = UUID.randomUUID(),
			address = "host-2.example.com",
			capabilities = testHostCapabilities.copy(
					cpus = listOf(testCpu, testCpu, testCpu, testCpu),
					totalMemory = "512 GB".toSize(),
					storageCapabilities = listOf(gvinumDisk)
			)
	)

	val vm1 = testVm.copy(
			id = UUID.randomUUID(),
			name = "vm-1",
			nrOfCpus = 2,
			memory = Range("512 MB".toSize(), "2 GB".toSize())
	)

	val vm2 = testVm.copy(
			id = UUID.randomUUID(),
			name = "vm-2",
			nrOfCpus = 1,
			memory = Range("1 GB".toSize(), "1 GB".toSize()),
			expectations = listOf(
					CoreDedicationExpectation(level = ExpectationLevel.DealBreaker)
			)
	)

	val vDisk1 = testVirtualDisk.copy(
			id = UUID.randomUUID(),
			size = "128 GB".toSize()
	)

	val vDisk2 = testVirtualDisk.copy(
			id = UUID.randomUUID(),
			size = "128 GB".toSize()
	)

	@Test
	fun basicBalanceReport() {
		hostCache!!.put(host1.id, host1)
		hostCache!!.put(host2.id, host2)
		vmCache!!.put(vm1.id, vm1)
		vmCache!!.put(vm2.id, vm2)
		vdiskCache!!.put(vDisk1.id, vDisk1)
		vdiskCache!!.put(vDisk2.id, vDisk2)
		vdiskDynCache!!.put(vDisk1.id, VirtualStorageDeviceDynamic(
				id = vDisk1.id,
				actualSize = "64 GB".toSize(),
				allocation = VirtualStorageGvinumAllocation(
						hostId = host2.id,
						configuration = SimpleGvinumConfiguration(diskId = gvinumDisk.id)
				)
		))

		val report = StatisticsDaoImpl(hostCache!!, vmCache!!, vdiskCache!!, vdiskDynCache!!).basicBalanceReport()

		assertEquals(2, report.totalHosts)
		assertEquals("256 GB".toSize() + "512 GB".toSize(), report.totalHostMemory)
		assertEquals("512 MB".toSize() + "1 GB".toSize(), report.totalMinVmMemory)
		assertEquals("2 GB".toSize() + "1 GB".toSize(), report.totalMaxVmMemory)

		assertEquals(6, report.totalHostCpus)
		assertEquals(vm1.nrOfCpus + vm2.nrOfCpus, report.totalVmCpus)
		assertEquals(vm2.nrOfCpus, report.totalDedicatedVmCpus)

		assertEquals("512 GB".toSize(), report.totalHostStorage)
		assertEquals(vDisk1.size + vDisk2.size, report.totalDiskStorageRequested)
		assertEquals("64 GB".toSize(), report.totalDiskStorageActual)
	}

	@Test
	fun basicBalanceReportWithEmpty() {
		val report = StatisticsDaoImpl(hostCache!!, vmCache!!, vdiskCache!!, vdiskDynCache!!).basicBalanceReport()

		assertEquals(0, report.totalHosts)
		assertEquals("0 GB".toSize(), report.totalHostMemory)
		assertEquals("0 MB".toSize(), report.totalMinVmMemory)
		assertEquals("0 GB".toSize(), report.totalMaxVmMemory)

		assertEquals(0, report.totalHostCpus)
		assertEquals(0, report.totalVmCpus)
		assertEquals(0, report.totalDedicatedVmCpus)

		assertEquals("0 GB".toSize(), report.totalHostStorage)

		assertEquals("0 GB".toSize(), report.totalDiskStorageRequested)
	}

}