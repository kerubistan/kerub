package com.github.kerubistan.kerub.model.dynamic

import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals

class VirtualStorageLvmAllocationTest {
	@Test
	fun validation() {
		assertThrows<IllegalStateException>("Size musn't be negative") {
			VirtualStorageLvmAllocation(
					hostId = UUID.randomUUID(),
					capabilityId = UUID.randomUUID(),
					actualSize = (-1).TB,
					path = "",
					vgName = "vg-1"
			)
		}
		assertThrows<IllegalStateException>("Number of mirrors must be at least 0") {
			VirtualStorageLvmAllocation(
					hostId = UUID.randomUUID(),
					capabilityId = UUID.randomUUID(),
					actualSize = (-1).TB,
					path = "",
					vgName = "vg-1",
					mirrors = -1
			)
		}
	}

	@Test
	fun getRedundancyLevel() {
		assertEquals(
				1,
				VirtualStorageLvmAllocation(
						hostId = UUID.randomUUID(),
						capabilityId = UUID.randomUUID(),
						actualSize = 1.TB,
						path = "",
						vgName = "vg-1",
						mirrors = 1
				).getRedundancyLevel()
		)
	}
}