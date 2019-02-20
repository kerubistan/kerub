package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.dynamic.gvinum.MirroredGvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.testHost
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID.randomUUID

class VirtualStorageGvinumAllocationTest {

	@Test
	fun getRedundancyLevel() {
		assertEquals(
				0.toByte(),
				VirtualStorageGvinumAllocation(
						hostId = testHost.id,
						actualSize = 1.GB,
						capabilityId = randomUUID(),
						configuration = SimpleGvinumConfiguration(
								diskName = "/dev/sda"
						)
				).getRedundancyLevel())
		assertEquals(
				1.toByte(),
				VirtualStorageGvinumAllocation(
						hostId = testHost.id,
						actualSize = 1.GB,
						capabilityId = randomUUID(),
						configuration = MirroredGvinumConfiguration(
								disks = listOf("/dev/sda", "/dev/sdb")
						)
				).getRedundancyLevel())
	}
}