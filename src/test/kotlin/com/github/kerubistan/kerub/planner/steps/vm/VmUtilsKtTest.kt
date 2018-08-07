package com.github.kerubistan.kerub.planner.steps.vm

import com.github.kerubistan.kerub.GB
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
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VmUtilsKtTest {

	@Test
	fun virtualStorageLinkInfo() {
		assertEquals(listOf<VirtualStorageLinkInfo>(), virtualStorageLinkInfo(OperationalState.fromLists(), listOf(), testHost.id))
	}

	@Test
	fun virtualStorageLinkInfoWithLocal() {
		val allocation = VirtualStorageLvmAllocation(
				hostId = testHost.id,
				vgName = "test-vg",
				path = "/dev/test-vg/${testDisk.id}",
				actualSize = 10.GB
		)
		val dynamic = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(
						allocation
				)
		)
		val hostDyn = HostDynamic(id = testHost.id, status = HostStatus.Up)
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
										stat = testHost,
										dynamic = hostDyn,
										config = HostConfiguration(id = testHost.id)
								),
								link = storageLink,
								hostServiceUsed = null
						)
				),
				virtualStorageLinkInfo(
						OperationalState.fromLists(
								hosts = listOf(testHost),
								hostDyns = listOf(hostDyn),
								vStorage = listOf(testDisk),
								vStorageDyns = listOf(
										dynamic
								)
						),
						listOf(
								storageLink
						), testHost.id
				)
		)
	}

	@Test
	fun virtualStorageLinkInfoWithRemoteIscsi() {
		val allocation = VirtualStorageLvmAllocation(
				hostId = testHost.id,
				vgName = "test-vg",
				path = "/dev/test-vg/${testDisk.id}",
				actualSize = 10.GB
		)
		val dynamic = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(
						allocation
				)
		)
		val hostDyn = HostDynamic(id = testHost.id, status = HostStatus.Up)
		val storageLink = VirtualStorageLink(
				device = DeviceType.disk,
				readOnly = false,
				bus = BusType.sata,
				virtualStorageId = testDisk.id
		)
		val hostConfiguration = HostConfiguration(
				id = testHost.id,
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
										stat = testHost,
										dynamic = hostDyn,
										config = HostConfiguration(
												id = testHost.id,
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
								hosts = listOf(testHost),
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
											fileName = "test.qcow2",
											mountPoint = "/kerub"
									),
									storageHost = HostDataCollection(
											stat = testHost
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