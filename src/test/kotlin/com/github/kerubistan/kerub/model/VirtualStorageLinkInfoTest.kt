package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.testOtherHost
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class VirtualStorageLinkInfoTest {
	@Test
	fun validation() {
		assertThrows<IllegalStateException> {
			val lvmCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					size = 2.TB,
					volumeGroupName = "testVg",
					physicalVolumes = mapOf("/dev/sda" to 2.TB)
			)
			VirtualStorageLinkInfo(
					device = VirtualStorageDataCollection(
							stat = testDisk,
							dynamic = VirtualStorageDeviceDynamic(
									id = testDisk.id,
									allocations = listOf()
							)
					),
					hostServiceUsed = null,
					link = VirtualStorageLink(
							device = DeviceType.disk,
							virtualStorageId = testDisk.id,
							bus = BusType.sata
					),
					storageHost = HostDataCollection(
							stat = testHost.copy(
									capabilities = testHostCapabilities.copy(
											storageCapabilities = listOf(
													lvmCapability
											)
									)
							)
					),
					allocation = VirtualStorageLvmAllocation(
							hostId = testOtherHost.id,
							vgName = "test-vg",
							path = "/dev/test-vg/test-lv",
							actualSize = 10.GB,
							capabilityId = lvmCapability.id
					)
			)
		}
		assertThrows<IllegalStateException> {
			val lvmCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					size = 2.TB,
					volumeGroupName = "testVg",
					physicalVolumes = mapOf("/dev/sda" to 2.TB)
			)
			VirtualStorageLinkInfo(
					device = VirtualStorageDataCollection(
							stat = testDisk,
							dynamic = VirtualStorageDeviceDynamic(
									id = testDisk.id,
									allocations = listOf()
							)
					),
					hostServiceUsed = null,
					link = VirtualStorageLink(
							device = DeviceType.disk,
							virtualStorageId = testCdrom.id,
							bus = BusType.sata
					),
					storageHost = HostDataCollection(
							stat = testHost.copy(
									capabilities = testHostCapabilities.copy(
											storageCapabilities = listOf(
													lvmCapability
											)
									)
							)
					),
					allocation = VirtualStorageLvmAllocation(
							hostId = testHost.id,
							vgName = "test-vg",
							path = "/dev/test-vg/test-lv",
							actualSize = 10.GB,
							capabilityId = lvmCapability.id
					)
			)

		}
		assertThrows<IllegalStateException> {
			val lvmCapability = LvmStorageCapability(
					id = UUID.randomUUID(),
					size = 2.TB,
					volumeGroupName = "testVg",
					physicalVolumes = mapOf("/dev/sda" to 2.TB)
			)
			VirtualStorageLinkInfo(
					device = VirtualStorageDataCollection(
							stat = testDisk,
							dynamic = VirtualStorageDeviceDynamic(
									id = testDisk.id,
									allocations = listOf()
							)
					),
					hostServiceUsed = IscsiService(vstorageId = testOtherHost.id),
					link = VirtualStorageLink(
							device = DeviceType.disk,
							virtualStorageId = testDisk.id,
							bus = BusType.sata
					),
					storageHost = HostDataCollection(
							stat = testHost.copy(
									capabilities = testHostCapabilities.copy(
											storageCapabilities = listOf(
													lvmCapability
											)
									)
							)),
					allocation = VirtualStorageLvmAllocation(
							hostId = testHost.id,
							vgName = "test-vg",
							path = "/dev/test-vg/test-lv",
							actualSize = 10.GB,
							capabilityId = lvmCapability.id
					)
			)

		}

		assertThrows<IllegalStateException> {
			VirtualStorageLinkInfo(
					device = VirtualStorageDataCollection(
							stat = testDisk,
							dynamic = VirtualStorageDeviceDynamic(
									id = testDisk.id,
									allocations = listOf()
							)
					),
					hostServiceUsed = IscsiService(vstorageId = testDisk.id),
					link = VirtualStorageLink(
							device = DeviceType.disk,
							virtualStorageId = testDisk.id,
							bus = BusType.sata
					),
					storageHost = HostDataCollection(
							stat = testHost.copy(
									capabilities = testHostCapabilities.copy(
											storageCapabilities = listOf()
									)
							)
					),
					allocation = VirtualStorageLvmAllocation(
							hostId = testHost.id,
							vgName = "test-vg",
							path = "/dev/test-vg/test-lv",
							actualSize = 10.GB,
							// not registered, so this should cause a validation exception
							capabilityId = testLvmCapability.id
					)
			)

		}

		val lvmCapability = LvmStorageCapability(
				id = UUID.randomUUID(),
				size = 2.TB,
				volumeGroupName = "testVg",
				physicalVolumes = mapOf("/dev/sda" to 2.TB)
		)

		VirtualStorageLinkInfo(
				device = VirtualStorageDataCollection(
						stat = testDisk,
						dynamic = VirtualStorageDeviceDynamic(
								id = testDisk.id,
								allocations = listOf()
						)
				),
				hostServiceUsed = IscsiService(vstorageId = testDisk.id),
				link = VirtualStorageLink(
						device = DeviceType.disk,
						virtualStorageId = testDisk.id,
						bus = BusType.sata
				),
				storageHost = HostDataCollection(
						stat = testHost.copy(
								capabilities = testHostCapabilities.copy(
										storageCapabilities = listOf(
												lvmCapability
										)
								)
						)
				),
				allocation = VirtualStorageLvmAllocation(
						hostId = testHost.id,
						vgName = "test-vg",
						path = "/dev/test-vg/test-lv",
						actualSize = 10.GB,
						capabilityId = lvmCapability.id
				)
		)


	}
}