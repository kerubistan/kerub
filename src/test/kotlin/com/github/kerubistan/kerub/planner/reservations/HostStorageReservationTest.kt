package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
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