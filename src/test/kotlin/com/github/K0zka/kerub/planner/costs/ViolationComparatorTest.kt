package com.github.K0zka.kerub.planner.costs

import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.expectations.NotSameHostExpectation
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class ViolationComparatorTest {

	@Test
	fun compare() {
		assertTrue {
			ViolationComparator.compare(
					Violation(VirtualMachineAvailabilityExpectation(level = ExpectationLevel.DealBreaker)),
					Violation(NotSameHostExpectation(level = ExpectationLevel.Want, otherVmId = UUID.randomUUID()))) > 0
		}
		assertTrue {
			ViolationComparator.compare(
					Violation(VirtualMachineAvailabilityExpectation(level = ExpectationLevel.DealBreaker)),
					Violation(VirtualMachineAvailabilityExpectation(level = ExpectationLevel.DealBreaker))) == 0
		}
	}
}