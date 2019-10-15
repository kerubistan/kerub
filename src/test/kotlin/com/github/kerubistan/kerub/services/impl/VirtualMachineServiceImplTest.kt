package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.TemplateDao
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.model.Asset
import com.github.kerubistan.kerub.model.Template
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.security.mockAccessGranted
import com.github.kerubistan.kerub.services.VmFromTemplateRequest
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import java.util.UUID
import java.util.UUID.randomUUID

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
		whenever(dao[eq(existingId)]).thenReturn(vm)
		doAnswer {
			checkExpectation(it, true)
		}.whenever(dao).update(Mockito.any<VirtualMachine>() ?: vm)
		VirtualMachineServiceImpl(dao, accessController, dynDao, mock(), mock()).startVm(existingId)
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
		whenever(dao[eq(existingId) ?: existingId]).thenReturn(vm)
		doAnswer {
			checkExpectation(it, true)
		}.`when`(dao).update(Mockito.any<VirtualMachine>() ?: vm)
		VirtualMachineServiceImpl(dao, accessController, dynDao, mock(), mock()).startVm(existingId)
	}

	@Test
	fun stopVmWithExistingExpectation() {
		checkStopVm(VirtualMachine(id = existingId, expectations = listOf(
				VirtualMachineAvailabilityExpectation(up = true)
		), name = "test", nrOfCpus = 1))
	}

	@Test
	fun createFromTemplate() {
		val templateDao = mock<TemplateDao>()
		val virtualStorageDeviceDao = mock<VirtualStorageDeviceDao>()
		val template = Template(
				vm = testVm.copy(),
				name = "test-template",
				id = randomUUID(),
				vmNamePrefix = "test-"
		)
		whenever(templateDao[eq(template.id)]).thenReturn(template)
		accessController.mockAccessGranted(template)
		VirtualMachineServiceImpl(dao, accessController, dynDao, templateDao, virtualStorageDeviceDao)
				.createFromTemplate(
						VmFromTemplateRequest(
								vmName = "nice new vm",
								templateId = template.id
						)
				)

		verify(templateDao)[eq(template.id)]
		verify(virtualStorageDeviceDao, never()).add(any()) // because there are no storage devices linked to the vm
	}

	@Test
	fun createFromTemplateWithDisks() {
		val templateDao = mock<TemplateDao>()
		val virtualStorageDeviceDao = mock<VirtualStorageDeviceDao>()
		val template = Template(
				vm = testVm.copy(
						virtualStorageLinks = listOf(
								VirtualStorageLink(
										virtualStorageId = testCdrom.id,
										bus = BusType.ide,
										device = DeviceType.cdrom,
										readOnly = true
								),
								VirtualStorageLink(
										virtualStorageId = testDisk.id,
										device = DeviceType.disk,
										bus = BusType.virtio,
										readOnly = false
								)
						)
				),
				name = "test-template",
				id = randomUUID(),
				vmNamePrefix = "test-"
		)
		whenever(templateDao[eq(template.id)]).thenReturn(template)
		whenever(virtualStorageDeviceDao[eq(testDisk.id)]).thenReturn(testDisk)
		whenever(virtualStorageDeviceDao[eq(testCdrom.id)]).thenReturn(testCdrom)
		accessController.mockAccessGranted(template)
		accessController.mockAccessGranted(testCdrom)
		accessController.mockAccessGranted(testDisk)
		VirtualMachineServiceImpl(dao, accessController, dynDao, templateDao, virtualStorageDeviceDao)
				.createFromTemplate(
						VmFromTemplateRequest(
								vmName = "nice new vm",
								templateId = template.id
						)
				)

		verify(templateDao)[eq(template.id)]
		verify(virtualStorageDeviceDao, times(1)).addAll(argThat { size == 1 }) // cloned only the rw disk
	}

}