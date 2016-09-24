package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import java.util.UUID

class VirtualMachineServiceImplTest {

	val existingId = UUID.randomUUID()
	val notExistingId = UUID.randomUUID()

	val dao: VirtualMachineDao = mock()

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
		whenever(dao.get(eq(existingId))).thenReturn(vm)
		doAnswer {
			checkExpectation(it, true)
		}.whenever(dao).update(Mockito.any<VirtualMachine>() ?: vm)
		VirtualMachineServiceImpl(dao).startVm(existingId)
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
		whenever(dao.get(eq(existingId) ?: existingId)).thenReturn(vm)
		doAnswer {
			checkExpectation(it, true)
		}.`when`(dao).update(Mockito.any<VirtualMachine>() ?: vm)
		VirtualMachineServiceImpl(dao).startVm(existingId)
	}

	@Test
	fun stopVmWithExistingExpectation() {
		checkStopVm(VirtualMachine(id = existingId, expectations = listOf(
				VirtualMachineAvailabilityExpectation(up = true)
		), name = "test", nrOfCpus = 1))
	}

}