package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.model.expectations.ChassisManufacturerExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import kotlin.test.assertTrue

class ChassisManufacturerExpectationViolationDetectorTest {
	@Test
	fun check() {
		assertTrue("Not running VM") {
			val expectation = ChassisManufacturerExpectation(
					manufacturer = "TEST-MANUFACTURER"
			)
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)
			ChassisManufacturerExpectationViolationDetector.check(
					vm,
					expectation,
					OperationalState.fromLists(
							vms = listOf(vm)
					)
			)
		}
	}

}