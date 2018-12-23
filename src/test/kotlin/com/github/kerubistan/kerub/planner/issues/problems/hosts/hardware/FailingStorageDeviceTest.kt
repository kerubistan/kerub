package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class FailingStorageDeviceTest {
	@Test
	fun validation() {
		assertThrows<IllegalStateException>("If the host does not have the capability registered, that's no good") {
			FailingStorageDevice(
					host = testHost,
					device = "sda",
					storageCapability = testLvmCapability
			)
		}
	}
}