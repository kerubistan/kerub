package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.expectations.NotSameHostExpectation
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.now
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class ViolationTest {
	@Test
	fun validations() {
		val otherVmId = UUID.randomUUID()
		assertThrows<IllegalStateException> {
			Violation(
					entity = testVm.copy(expectations = listOf()),
					start = now() - 1000,
					violatedConstraint = NotSameHostExpectation(otherVmId = otherVmId)
			)
		}
		assertThrows<IllegalStateException> {
			Violation(
					entity = testVm.copy(expectations = listOf(NotSameHostExpectation(otherVmId = otherVmId))),
					start = now() - 1000,
					end = now() - 2000,
					violatedConstraint = NotSameHostExpectation(otherVmId = otherVmId)
			)
		}
	}
}