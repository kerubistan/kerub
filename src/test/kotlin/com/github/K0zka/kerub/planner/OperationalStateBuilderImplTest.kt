package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.data.VirtualStorageDeviceDao
import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.ControllerManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.model.controller.AssignmentType
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

@RunWith(MockitoJUnitRunner::class) class OperationalStateBuilderImplTest {

	val controllerManager: ControllerManager = mock()
	val assignmentDao: AssignmentDao = mock()
	val hostDyn: HostDynamicDao = mock()
	val hostCfg: HostConfigurationDao = mock()
	val hostDao: HostDao = mock()
	var vmDao : VirtualMachineDao = mock()
	var vmDynDao : VirtualMachineDynamicDao = mock()
	var vStorageDao : VirtualStorageDeviceDao = mock()
	var vStorageDynDao: VirtualStorageDeviceDynamicDao = mock()

	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	val vm = VirtualMachine(
			id = UUID.randomUUID(),
			name = "vm-1"
	)

	val vmDyn = VirtualMachineDynamic(
			id = vm.id,
			hostId = host.id,
			status = VirtualMachineStatus.Up,
			memoryUsed = "256 MB".toSize()
	)

	@Test
	fun buildState() {
		val assignments = listOf(
				Assignment(
						controller = "TEST-CONTROLLER",
						entityId = vm.id,
						type = AssignmentType.vm
				),
				Assignment(
						controller = "TEST-CONTROLLER",
						entityId = vm.id,
						type = AssignmentType.host
				)
		)
		Mockito.`when`(controllerManager.getControllerId() ?: 0).thenReturn("TEST-CONTROLLER")
		Mockito.`when`(assignmentDao.listByController(Matchers.eq("TEST-CONTROLLER") ?: "")).thenReturn(assignments)
		Mockito.`when`(hostDao.get(Matchers.any(UUID::class.java) ?: host.id)).thenReturn(host)
		Mockito.`when`(vmDao.get(Matchers.any(UUID::class.java) ?: vm.id)).thenReturn(vm)
		Mockito.`when`(vmDynDao.get(Matchers.any(UUID::class.java) ?: vm.id)).thenReturn(vmDyn)

		val state = OperationalStateBuilderImpl(
				controllerManager,
				assignmentDao,
				hostDyn,
				hostCfg,
				hostDao,
				vStorageDao,
				vStorageDynDao,
				vmDao,
				vmDynDao).buildState()

		Assert.assertTrue(state.hosts.isNotEmpty())
		Assert.assertTrue(state.vms.isNotEmpty())
		Assert.assertTrue(state.vms.any { it.value.dynamic != null })

		Mockito.verify(controllerManager).getControllerId()
		Mockito.verify(assignmentDao).listByController(Matchers.eq("TEST-CONTROLLER") ?: "")
	}
}