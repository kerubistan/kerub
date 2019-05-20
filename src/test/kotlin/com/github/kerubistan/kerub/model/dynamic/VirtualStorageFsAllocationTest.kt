package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
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
					mountPoint = "",
					type = VirtualDiskFormat.raw,
					fileName = ""
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
						fileName = "",
						type = VirtualDiskFormat.raw,
						mountPoint = "/kerub"
				).getRedundancyLevel()
		)
	}
}