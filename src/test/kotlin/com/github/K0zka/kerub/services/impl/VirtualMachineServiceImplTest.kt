package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class VirtualMachineServiceImplTest {

	val existingId = UUID.randomUUID()
	val notExistingId = UUID.randomUUID()

	@Mock
	var dao: VirtualMachineDao? = null

	@Test
	fun startVm() {
		checkStartVm(VirtualMachine(id = existingId, expectations = listOf(), name = "test", nrOfCpus = 1))
	}

	@Test
	fun startVmWithExistingExpectation() {
		checkStartVm(VirtualMachine(id = existingId, expectations = listOf(
				VirtualMachineAvailabilityExpectation(up = false)
		), name = "test", nrOfCpus = 1))
	}

	private fun checkStartVm(vm: VirtualMachine) {
		Mockito.`when`(dao!!.get(Mockito.eq(existingId) ?: existingId)).thenReturn(vm)
		Mockito.doAnswer {
			checkExpectation(it, true)
		}.`when`(dao!!).update(Mockito.any(VirtualMachine::class.java) ?: vm)
		VirtualMachineServiceImpl(dao!!).startVm(existingId)
	}

	private fun checkExpectation(invocation: InvocationOnMock, expected: Boolean) {
		val updateVm = invocation.arguments[0] as VirtualMachine
		val expectation = (updateVm.expectations.first { it is VirtualMachineAvailabilityExpectation }) as VirtualMachineAvailabilityExpectation
		assertEquals(expected, expectation.up)
	}

	@Test
	fun stopVm() {
		checkStopVm(VirtualMachine(id = existingId, expectations = listOf(), name = "test", nrOfCpus = 1))
	}

	private fun checkStopVm(vm: VirtualMachine) {
		Mockito.`when`(dao!!.get(Mockito.eq(existingId) ?: existingId)).thenReturn(vm)
		Mockito.doAnswer {
			checkExpectation(it, true)
		}.`when`(dao!!).update(Mockito.any(VirtualMachine::class.java) ?: vm)
		VirtualMachineServiceImpl(dao!!).startVm(existingId)
	}

	@Test
	fun stopVmWithExistingExpectation() {
		checkStopVm(VirtualMachine(id = existingId, expectations = listOf(
				VirtualMachineAvailabilityExpectation(up = true)
		), name = "test", nrOfCpus = 1))
	}

}