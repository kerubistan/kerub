package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID.randomUUID

class VirtualStorageFsAllocationTest {

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