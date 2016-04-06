package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.Plan
import org.junit.Test
import kotlin.test.assertTrue

class CompositeStepFactoryTest {

	@Test
	fun produce() {
		val vm = VirtualMachine(
				name = "test-vm",
				expectations = listOf(VirtualMachineAvailabilityExpectation(up = true))
		)
		val plan = Plan(
				state = OperationalState.fromLists(
						vms = listOf(vm),
						hostDyns = listOf()
				),
				steps = listOf()
		)
		val steps = CompositeStepFactory.produce(plan)

		//TODO:
	}

	@Test
	fun produceFromEmpty() {
		val plan = Plan(
				state = OperationalState.fromLists(),
				steps = listOf()
		)
		val steps = CompositeStepFactory.produce(plan)

		assertTrue { steps.isEmpty() }
	}
}