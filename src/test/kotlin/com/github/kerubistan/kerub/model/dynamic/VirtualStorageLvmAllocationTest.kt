package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class VirtualStorageLvmAllocationTest {
	@Test
	fun validation() {
		assertThrows<IllegalStateException> {
			VirtualStorageLvmAllocation(
					hostId = UUID.randomUUID(),
					capabilityId = UUID.randomUUID(),
					actualSize = (-1).TB,
					path = "",
					vgName = "vg-1"
			)
		}
	}
}