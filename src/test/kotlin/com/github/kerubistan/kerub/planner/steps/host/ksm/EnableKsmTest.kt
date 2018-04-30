package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.testHost
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EnableKsmTest {

	@Test
	fun isInverseOf() {
		assertTrue("correct pair") {
			EnableKsm(host = testHost, cycles = 10).isInverseOf(DisableKsm(host = testHost))
		}
		assertFalse("different hosts - should not pass") {
			EnableKsm(host = testHost, cycles = 10).isInverseOf(DisableKsm(host = testHost.copy(id = UUID.randomUUID())))
		}
	}
}