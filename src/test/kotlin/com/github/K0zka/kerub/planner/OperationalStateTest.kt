package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class OperationalStateTest {
	@Test
	fun getNrOfUnsatisfiedExpectationsWithEmptyState() {
		Assert.assertEquals(OperationalState().getNrOfUnsatisfiedExpectations(ExpectationLevel.DealBreaker), 0)
		Assert.assertEquals(OperationalState().getNrOfUnsatisfiedExpectations(ExpectationLevel.Want), 0)
		Assert.assertEquals(OperationalState().getNrOfUnsatisfiedExpectations(ExpectationLevel.Wish), 0)
	}

	@Test
	fun getNrOfUnsatisfiedExpectationsWithSingleVmSingleDealBreaker() {
		val state = OperationalState.fromLists(
				vms = listOf(
						VirtualMachine(
								id = UUID.randomUUID(),
								name = "test-vm",
								expectations = listOf(
										VirtualMachineAvailabilityExpectation(up = true, level = ExpectationLevel.DealBreaker)
								                     )
						              )
				            )
		                                      )


		Assert.assertEquals(state.getNrOfUnsatisfiedExpectations(ExpectationLevel.DealBreaker), 1)
		Assert.assertEquals(state.getNrOfUnsatisfiedExpectations(ExpectationLevel.Want), 0)
		Assert.assertEquals(state.getNrOfUnsatisfiedExpectations(ExpectationLevel.Wish), 0)

	}


}