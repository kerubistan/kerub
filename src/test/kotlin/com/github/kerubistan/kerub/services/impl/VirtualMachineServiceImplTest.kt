package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.security.AssetAccessController
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

	private val existingId = UUID.randomUUID()

	private val dao: VirtualMachineDao = mock()
	private val dynDao: VirtualMachineDynamicDao = mock()
	private val accessController: AssetAccessController = mock()

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
		whenever(accessController.doAndCheck(any<() -> Asset>())).then { (it.arguments[0] as () -> Asset).invoke() }
		whenever(accessController.checkAndDo(any(), any<() -> Asset?>())).then { (it.arguments[1] as () -> Asset).invoke() }
		whenever(dao.get(eq(existingId))).thenReturn(vm)
		doAnswer {
			checkExpectation(it, true)
		}.whenever(dao).update(Mockito.any<VirtualMachine>() ?: vm)
		VirtualMachineServiceImpl(dao, accessController, dynDao).startVm(existingId)
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
		whenever(accessController.doAndCheck(any<() -> Asset>())).then { (it.arguments[0] as () -> Asset).invoke() }
		whenever(accessController.checkAndDo(any(), any<() -> Asset?>())).then { (it.arguments[1] as () -> Asset).invoke() }
		whenever(dao.get(eq(existingId) ?: existingId)).thenReturn(vm)
		doAnswer {
			checkExpectation(it, true)
		}.`when`(dao).update(Mockito.any<VirtualMachine>() ?: vm)
		VirtualMachineServiceImpl(dao, accessController, dynDao).startVm(existingId)
	}

	@Test
	fun stopVmWithExistingExpectation() {
		checkStopVm(VirtualMachine(id = existingId, expectations = listOf(
				VirtualMachineAvailabilityExpectation(up = true)
		), name = "test", nrOfCpus = 1))
	}

}