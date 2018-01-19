package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import kotlin.test.assertTrue

class PlanViolationDetectorImplTest {
	@Test
	fun listViolations() {
		assertTrue("a blank state should have no violations") {
			PlanViolationDetectorImpl().listViolations(Plan(
					OperationalState.fromLists(),
					listOf()
			)).isEmpty()
		}
		assertTrue("A vm needs to be up and running, but there is no host") {
			val expectation = VirtualMachineAvailabilityExpectation(up = true)
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)
			PlanViolationDetectorImpl().listViolations(Plan(OperationalState.fromLists(
					vms = listOf(vm)
			), listOf())).let {
				it.isNotEmpty()
						&& it.contains(vm)
						&& it[vm] == listOf(expectation)
			}
		}
	}

}