package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class VirtualStorageAllocationOnFailingStorageDeviceTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			VirtualStorageAllocationOnFailingStorageDevice(
					host = testHost,
					capability = testLvmCapability,
					allocation = VirtualStorageLvmAllocation(
							vgName = testLvmCapability.volumeGroupName,
							actualSize = testDisk.size,
							capabilityId = UUID.randomUUID(),
							hostId = testHost.id,
							path = "/dev/test-vg"
					),
					storageDevice = testDisk
			)
		}
		assertThrows<IllegalStateException> {
			VirtualStorageAllocationOnFailingStorageDevice(
					host = testHost,
					capability = testLvmCapability,
					allocation = VirtualStorageLvmAllocation(
							vgName = testLvmCapability.volumeGroupName,
							actualSize = testDisk.size,
							capabilityId = testLvmCapability.id,
							hostId = UUID.randomUUID(),
							path = "/dev/test-vg"
					),
					storageDevice = testDisk
			)
		}
	}
}