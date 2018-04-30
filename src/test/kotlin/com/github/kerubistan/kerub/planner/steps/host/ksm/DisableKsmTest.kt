package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.testHost
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DisableKsmTest {

	@Test
	fun isInverseOf() {
		assertTrue("correct inverse pairs") {
			DisableKsm(host = testHost).isInverseOf(EnableKsm(host = testHost, cycles = 10))
		}
		assertFalse("incorrect - host is different") {
			DisableKsm(host = testHost).isInverseOf(EnableKsm(host = testHost.copy(id = UUID.randomUUID()), cycles = 10))
		}
	}
}