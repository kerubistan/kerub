package com.github.kerubistan.kerub.planner.steps.vm

import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.VirtualStorageLinkInfo
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVm
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VmUtilsKtTest {

	@Test
	fun virtualStorageLinkInfo() {
		assertEquals(listOf<VirtualStorageLinkInfo>(), virtualStorageLinkInfo(OperationalState.fromLists(), listOf(), testHost.id))
	}

	@Test
	fun virtualStorageLinkInfoWithLocal() {
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								testLvmCapability
						)
				)
		)
		val allocation = VirtualStorageLvmAllocation(
				hostId = host.id,
				vgName = "test-vg",
				path = "/dev/test-vg/${testDisk.id}",
				actualSize = 10.GB,
				capabilityId = testLvmCapability.id
		)
		val dynamic = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(
						allocation
				)
		)
		val hostDyn = HostDynamic(id = host.id, status = HostStatus.Up)
		val storageLink = VirtualStorageLink(
				device = DeviceType.disk,
				readOnly = false,
				bus = BusType.sata,
				virtualStorageId = testDisk.id
		)
		assertEquals(
				listOf(
						VirtualStorageLinkInfo(
								device = VirtualStorageDataCollection(
										dynamic = dynamic,
										stat = testDisk
								),
								allocation = allocation,
								storageHost = HostDataCollection(
										stat = host,
										dynamic = hostDyn,
										config = HostConfiguration(id = host.id)
								),
								link = storageLink,
								hostServiceUsed = null
						)
				),
				virtualStorageLinkInfo(
						OperationalState.fromLists(
								hosts = listOf(host),
								hostDyns = listOf(hostDyn),
								vStorage = listOf(testDisk),
								vStorageDyns = listOf(
										dynamic
								)
						),
						listOf(
								storageLink
						), host.id
				)
		)
	}

	@Test
	fun virtualStorageLinkInfoWithRemoteIscsi() {
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(testLvmCapability)
				)
		)
		val allocation = VirtualStorageLvmAllocation(
				hostId = host.id,
				vgName = "test-vg",
				path = "/dev/test-vg/${testDisk.id}",
				actualSize = 10.GB,
				capabilityId = testLvmCapability.id
		)
		val dynamic = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(
						allocation
				)
		)
		val hostDyn = HostDynamic(id = host.id, status = HostStatus.Up)
		val storageLink = VirtualStorageLink(
				device = DeviceType.disk,
				readOnly = false,
				bus = BusType.sata,
				virtualStorageId = testDisk.id
		)
		val hostConfiguration = HostConfiguration(
				id = host.id,
				services = listOf(
						IscsiService(vstorageId = testDisk.id)
				)
		)
		assertEquals(
				listOf(
						VirtualStorageLinkInfo(
								device = VirtualStorageDataCollection(
										dynamic = dynamic,
										stat = testDisk
								),
								allocation = allocation,
								storageHost = HostDataCollection(
										stat = host,
										dynamic = hostDyn,
										config = HostConfiguration(
												id = host.id,
												services = listOf(
														IscsiService(vstorageId = testDisk.id)
												)
										)
								),
								link = storageLink,
								hostServiceUsed = IscsiService(vstorageId = testDisk.id)
						)
				),
				virtualStorageLinkInfo(
						OperationalState.fromLists(
								hosts = listOf(host),
								hostDyns = listOf(hostDyn),
								hostCfgs = listOf(
										hostConfiguration
								),
								vStorage = listOf(testDisk),
								vStorageDyns = listOf(
										dynamic
								)
						),
						listOf(
								storageLink
						),
						testOtherHost.id
				)
		)
	}

	@Test
	fun virtualStorageLinkInfoWithRemoteNfs() {
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(testFsCapability)
				)
		)
		val allocation = VirtualStorageFsAllocation(
				hostId = host.id,
				actualSize = 10.GB,
				mountPoint = "/kerub",
				type = VirtualDiskFormat.qcow2,
				fileName = "/kerub/${testDisk.id}.qcow2",
				capabilityId = testFsCapability.id
		)
		val dynamic = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(
						allocation
				)
		)
		val storageHostDyn = HostDynamic(id = host.id, status = HostStatus.Up)
		val storageClientHostDyn = HostDynamic(id = testOtherHost.id, status = HostStatus.Up)
		val storageLink = VirtualStorageLink(
				device = DeviceType.disk,
				readOnly = false,
				bus = BusType.sata,
				virtualStorageId = testDisk.id
		)
		val storageHostConfiguration = HostConfiguration(
				id = host.id,
				services = listOf(
						NfsDaemonService(),
						NfsService(directory = "/kerub", write = true)
				)
		)
		val storageClientConfiguration = HostConfiguration(
				id = testOtherHost.id,
				services = listOf(
						NfsMount(remoteDirectory = "/kerub", remoteHostId = host.id, localDirectory = "/mnt/${host.id}/kerub")
				)
		)
		assertEquals(
				listOf(
						VirtualStorageLinkInfo(
								device = VirtualStorageDataCollection(
										dynamic = dynamic,
										stat = testDisk
								),
								allocation = allocation,
								storageHost = HostDataCollection(
										stat = host,
										dynamic = storageHostDyn,
										config = storageHostConfiguration
								),
								link = storageLink,
								hostServiceUsed = NfsService(directory = "/kerub", write = true)
						)
				),
				virtualStorageLinkInfo(
						OperationalState.fromLists(
								hosts = listOf(host, testOtherHost),
								hostDyns = listOf(storageHostDyn,storageClientHostDyn),
								hostCfgs = listOf(
										storageHostConfiguration,
										storageClientConfiguration
								),
								vStorage = listOf(testDisk),
								vStorageDyns = listOf(
										dynamic
								)
						),
						listOf(
								storageLink
						),
						testOtherHost.id
				)
		)
	}

	@Test
	fun virtualStorageLinkInfoWithRemoteNfsNotMounted() {
		val allocation = VirtualStorageFsAllocation(
				hostId = testHost.id,
				actualSize = 10.GB,
				mountPoint = "/kerub",
				type = VirtualDiskFormat.qcow2,
				fileName = "/kerub/${testDisk.id}.qcow2",
				capabilityId = testFsCapability.id
		)
		val dynamic = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(
						allocation
				)
		)
		val storageHostDyn = HostDynamic(id = testHost.id, status = HostStatus.Up)
		val storageClientHostDyn = HostDynamic(id = testOtherHost.id, status = HostStatus.Up)
		val storageLink = VirtualStorageLink(
				device = DeviceType.disk,
				readOnly = false,
				bus = BusType.sata,
				virtualStorageId = testDisk.id
		)
		val storageHostConfiguration = HostConfiguration(
				id = testHost.id,
				services = listOf(
						NfsDaemonService(),
						NfsService(directory = "/kerub", write = true)
				)
		)
		val storageClientConfiguration = HostConfiguration(
				id = testOtherHost.id,
				services = listOf()
		)
		assertTrue(
				virtualStorageLinkInfo(
						OperationalState.fromLists(
								hosts = listOf(testHost, testOtherHost),
								hostDyns = listOf(storageHostDyn,storageClientHostDyn),
								hostCfgs = listOf(
										storageHostConfiguration,
										storageClientConfiguration
								),
								vStorage = listOf(testDisk),
								vStorageDyns = listOf(
										dynamic
								)
						),
						listOf(
								storageLink
						),
						testOtherHost.id
				).isEmpty()
		)
	}

	@Test
	fun allStorageAvailable() {
		assertTrue("no storage, should work") {
			allStorageAvailable(testVm, listOf())
		}
		assertFalse("all storage mapping missing") {
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									device = DeviceType.disk,
									bus = BusType.sata,
									virtualStorageId = testDisk.id
							)
					)
			)
			allStorageAvailable(vm, listOf())
		}
		assertFalse("one storage mapping missing") {
			val vm = testVm.copy(
					virtualStorageLinks = listOf(
							VirtualStorageLink(
									device = DeviceType.disk,
									bus = BusType.sata,
									virtualStorageId = testDisk.id
							),
							VirtualStorageLink(
									device = DeviceType.disk,
									bus = BusType.sata,
									virtualStorageId = testCdrom.id
							)
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			allStorageAvailable(vm,
					listOf(
							VirtualStorageLinkInfo(
									device = VirtualStorageDataCollection(
											stat = testDisk,
											dynamic = null
									),
									allocation = VirtualStorageFsAllocation(
											hostId = testHost.id,
											actualSize = 10.GB,
											type = VirtualDiskFormat.qcow2,
											fileName = "/kerub/test.qcow2",
											mountPoint = "/kerub",
											capabilityId = testFsCapability.id
									),
									storageHost = HostDataCollection(
											stat = host
									),
									link = VirtualStorageLink(
											device = DeviceType.disk,
											bus = BusType.sata,
											virtualStorageId = testDisk.id
									),
									hostServiceUsed = null
							)
					)
			)
		}
	}

}