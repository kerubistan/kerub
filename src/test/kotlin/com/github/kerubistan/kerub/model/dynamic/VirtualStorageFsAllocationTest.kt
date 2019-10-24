package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

class VirtualStorageFsAllocationTest {

	@Test
	fun validations() {
		assertThrows<IllegalStateException>("actual size validation") {
			VirtualStorageFsAllocation(
					hostId = testHost.id,
					capabilityId = testFsCapability.id,
					actualSize = (-1).toBigInteger(),
					mountPoint = "/kerub",
					type = VirtualDiskFormat.raw,
					fileName = "/kerub/${testDisk.id}.raw"
			)
		}
		assertThrows<IllegalArgumentException>("file name must be a full path") {
			VirtualStorageFsAllocation(
					hostId = testHost.id,
					capabilityId = testFsCapability.id,
					actualSize = 1.GB,
					mountPoint = "/kerub",
					type = VirtualDiskFormat.raw,
					fileName = "${testDisk.id}.raw"
			)
		}
		assertThrows<IllegalArgumentException>("file name must end with file type") {
			VirtualStorageFsAllocation(
					hostId = testHost.id,
					capabilityId = testFsCapability.id,
					actualSize = 1.GB,
					mountPoint = "/kerub",
					type = VirtualDiskFormat.raw,
					fileName = "/kerub/${testDisk.id}"
			)
		}
	}

	@Test
	fun getRedundancyLevel() {
		assertEquals(
				0.toByte(),
				VirtualStorageFsAllocation(
						capabilityId = randomUUID(),
						actualSize = 1.TB,
						hostId = randomUUID(),
						fileName = "/kerub/${randomUUID()}.raw",
						type = VirtualDiskFormat.raw,
						mountPoint = "/kerub"
				).getRedundancyLevel()
		)
	}
}