package com.github.kerubistan.kerub.model

import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class LvmStorageCapabilityTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException>("invalid size") {
			LvmStorageCapability(
					id = UUID.randomUUID(),
					size = (-1).toBigInteger(),
					physicalVolumes = mapOf("/dev/sda" to 1.TB),
					volumeGroupName = "vg-1"
			)
		}
		assertThrows<IllegalStateException>("no PVs") {
			LvmStorageCapability(
					id = UUID.randomUUID(),
					size = 1.TB,
					physicalVolumes = mapOf(),
					volumeGroupName = "vg-1"
			)
		}
	}
}