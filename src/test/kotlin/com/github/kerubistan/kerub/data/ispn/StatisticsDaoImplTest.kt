package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.ControllerConfigDao
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapabilityDrive
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.Range
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.model.expectations.CoreDedicationExpectation
import com.github.kerubistan.kerub.testCpu
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVirtualDisk
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.MB
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
	protected var hostDynCache: Cache<UUID, HostDynamic>? = null
	protected var vdiskCache: Cache<UUID, VirtualStorageDevice>? = null
	protected var vdiskDynCache: Cache<UUID, VirtualStorageDeviceDynamic>? = null
	protected var controllerConfigDao : ControllerConfigDao = mock()

	@Before
	fun setUp() {
		cacheManager = DefaultCacheManager()
		cacheManager!!.start()
		vmCache = getCleanCache("vmCache")
		hostCache = getCleanCache("hostCache")
		hostDynCache = getCleanCache("hostDynCache")
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
					totalMemory = 256.GB,
					storageCapabilities = listOf(
							LvmStorageCapability(
									id = UUID.randomUUID(),
									physicalVolumes = mapOf("/dev/sda" to 128.GB, "/dev/sdb" to 128.GB),
									size = 256.GB,
									volumeGroupName = "data-vg"
							)
					)
			)
	)

	val gvinumCapability = GvinumStorageCapability(
			id = UUID.randomUUID(),
			devices = listOf(
					GvinumStorageCapabilityDrive(
							size = 256.GB,
							name = "gvinum-disk-1",
							device = "/dev/blah"
					)
			)
	)
	val host2 = testHost.copy(
			id = UUID.randomUUID(),
			address = "host-2.example.com",
			capabilities = testHostCapabilities.copy(
					cpus = listOf(testCpu, testCpu, testCpu, testCpu),
					totalMemory = 512.GB,
					storageCapabilities = listOf(gvinumCapability)
			)
	)

	val vm1 = testVm.copy(
			id = UUID.randomUUID(),
			name = "vm-1",
			nrOfCpus = 2,
			memory = Range(512.MB, 2.GB)
	)

	val vm2 = testVm.copy(
			id = UUID.randomUUID(),
			name = "vm-2",
			nrOfCpus = 1,
			memory = Range(1.GB, 1.GB),
			expectations = listOf(
					CoreDedicationExpectation(level = ExpectationLevel.DealBreaker)
			)
	)

	val vDisk1 = testVirtualDisk.copy(
			id = UUID.randomUUID(),
			size = 128.GB
	)

	val vDisk2 = testVirtualDisk.copy(
			id = UUID.randomUUID(),
			size = 128.GB
	)

	@Test
	fun basicBalanceReport() {
		hostCache!![host1.id] = host1
		hostCache!![host2.id] = host2
		vmCache!![vm1.id] = vm1
		vmCache!![vm2.id] = vm2
		vdiskCache!![vDisk1.id] = vDisk1
		vdiskCache!![vDisk2.id] = vDisk2
		vdiskDynCache!![vDisk1.id] = VirtualStorageDeviceDynamic(
				id = vDisk1.id,
				allocations = listOf(VirtualStorageGvinumAllocation(
						hostId = host2.id,
						actualSize = 64.GB,
						configuration = SimpleGvinumConfiguration(diskName = gvinumCapability.devices.single().name),
						capabilityId = gvinumCapability.id
				))
		)
		whenever(controllerConfigDao.get()).thenReturn(
				ControllerConfig()
		)

		val report = StatisticsDaoImpl(
				hostCache!!,
				hostDynCache!!,
				vmCache!!,
				vdiskCache!!,
				vdiskDynCache!!,
				controllerConfigDao
		).basicBalanceReport()

		assertEquals(2, report.totalHosts)
		assertEquals(256.GB + 512.GB, report.totalHostMemory)
		assertEquals(512.MB + 1.GB, report.totalMinVmMemory)
		assertEquals(2.GB + 1.GB, report.totalMaxVmMemory)

		assertEquals(6, report.totalHostCpus)
		assertEquals(vm1.nrOfCpus + vm2.nrOfCpus, report.totalVmCpus)
		assertEquals(vm2.nrOfCpus, report.totalDedicatedVmCpus)

		assertEquals(512.GB, report.totalHostStorage)
		assertEquals(vDisk1.size + vDisk2.size, report.totalDiskStorageRequested)
		assertEquals(64.GB, report.totalDiskStorageActual)
	}

	@Test
	fun basicBalanceReportWithGvinumDisabled() {
		hostCache!![host1.id] = host1
		hostCache!![host2.id] = host2
		vmCache!![vm1.id] = vm1
		vmCache!![vm2.id] = vm2
		vdiskCache!![vDisk1.id] = vDisk1
		vdiskCache!![vDisk2.id] = vDisk2
		vdiskDynCache!![vDisk1.id] = VirtualStorageDeviceDynamic(
				id = vDisk1.id,
				allocations = listOf(VirtualStorageGvinumAllocation(
						hostId = host2.id,
						actualSize = "64 GB".toSize(),
						configuration = SimpleGvinumConfiguration(diskName = gvinumCapability.devices.single().name),
						capabilityId =gvinumCapability.id
				))
		)

		whenever(controllerConfigDao.get()).thenReturn(
				ControllerConfig(storageTechnologies = StorageTechnologiesConfig(gvinumCreateVolumeEnabled = false))
		)

		val report = StatisticsDaoImpl(
				hostCache!!,
				hostDynCache!!,
				vmCache!!,
				vdiskCache!!,
				vdiskDynCache!!,
				controllerConfigDao
		).basicBalanceReport()

		assertEquals(2, report.totalHosts)
		assertEquals("256 GB".toSize() + "512 GB".toSize(), report.totalHostMemory)
		assertEquals("512 MB".toSize() + "1 GB".toSize(), report.totalMinVmMemory)
		assertEquals("2 GB".toSize() + "1 GB".toSize(), report.totalMaxVmMemory)

		assertEquals(6, report.totalHostCpus)
		assertEquals(vm1.nrOfCpus + vm2.nrOfCpus, report.totalVmCpus)
		assertEquals(vm2.nrOfCpus, report.totalDedicatedVmCpus)

		assertEquals("256 GB".toSize(), report.totalHostStorage)
		assertEquals(vDisk1.size + vDisk2.size, report.totalDiskStorageRequested)
		assertEquals("64 GB".toSize(), report.totalDiskStorageActual)
	}

	@Test
	fun basicBalanceReportWithEmpty() {
		whenever(controllerConfigDao.get()).thenReturn(
				ControllerConfig()
		)
		val report = StatisticsDaoImpl(
				hostCache!!,
				hostDynCache!!,
				vmCache!!,
				vdiskCache!!,
				vdiskDynCache!!,
				controllerConfigDao
		).basicBalanceReport()

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