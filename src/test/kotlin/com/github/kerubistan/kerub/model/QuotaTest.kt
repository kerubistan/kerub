package com.github.kerubistan.kerub.model

import org.junit.Test
import org.junit.jupiter.api.assertThrows

class QuotaTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException>("invalid value for total memory") {
			Quota(
					totalMemory = (-1).toBigInteger()
			)
		}
		assertThrows<IllegalStateException>("invalid value for maxVmMemory") {
			Quota(
					maxVmMemory = (-1).toBigInteger()
			)
		}
		assertThrows<IllegalStateException>("invalid value for totalDiskSpace") {
			Quota(
					totalDiskSpace = (-1).toBigInteger()
			)
		}

	}
}