package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.model.dynamic.gvinum.MirroredGvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testGvinumCapability
import com.github.kerubistan.kerub.testHost
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID.randomUUID

class VirtualStorageGvinumAllocationTest : AbstractDataRepresentationTest<VirtualStorageGvinumAllocation>() {

	override val testInstances = listOf(
			VirtualStorageGvinumAllocation(
					capabilityId = testGvinumCapability.id,
					hostId = testFreeBsdHost.id,
					actualSize = 4.TB,
					configuration = SimpleGvinumConfiguration(
							diskName = "test-disk"
					)
			)
	)
	override val clazz = VirtualStorageGvinumAllocation::class.java

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