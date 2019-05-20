package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class HostStorageReservationTest {
	@Test
	fun validation() {
		assertThrows<IllegalStateException> {
			HostStorageReservation(
					host = testHost,
					reservedStorage = (-1).GB,
					storageCapabilityId = testLvmCapability.id)
		}
	}
}