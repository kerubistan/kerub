package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.expect
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
import com.github.kerubistan.kerub.testOtherHost
import org.junit.jupiter.api.Test

internal class VirtualStorageLinkInfoTest {
	@Test
	fun validation() {
		expect(IllegalStateException::class) {
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
							stat = testHost
					),
					allocation = VirtualStorageLvmAllocation(
							hostId = testOtherHost.id,
							vgName = "test-vg",
							path = "/dev/test-vg/test-lv",
							actualSize = 10.GB
					)
			)
		}
		expect(IllegalStateException::class) {
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
							stat = testHost
					),
					allocation = VirtualStorageLvmAllocation(
							hostId = testHost.id,
							vgName = "test-vg",
							path = "/dev/test-vg/test-lv",
							actualSize = 10.GB
					)
			)

		}
		expect(IllegalStateException::class) {
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
							stat = testHost
					),
					allocation = VirtualStorageLvmAllocation(
							hostId = testHost.id,
							vgName = "test-vg",
							path = "/dev/test-vg/test-lv",
							actualSize = 10.GB
					)
			)

		}

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
						stat = testHost
				),
				allocation = VirtualStorageLvmAllocation(
						hostId = testHost.id,
						vgName = "test-vg",
						path = "/dev/test-vg/test-lv",
						actualSize = 10.GB
				)
		)


	}
}