package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.data.AssignmentDao
import com.github.kerubistan.kerub.data.ControllerConfigDao
import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.ControllerManager
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.controller.Assignment
import com.github.kerubistan.kerub.model.controller.AssignmentType
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import java.util.UUID

class OperationalStateBuilderImplTest {

	val controllerManager: ControllerManager = mock()
	val assignmentDao: AssignmentDao = mock()
	val hostDyn: HostDynamicDao = mock()
	val hostCfg: HostConfigurationDao = mock()
	val hostDao: HostDao = mock()
	val vmDao : VirtualMachineDao = mock()
	val vmDynDao : VirtualMachineDynamicDao = mock()
	val vStorageDao : VirtualStorageDeviceDao = mock()
	val vStorageDynDao: VirtualStorageDeviceDynamicDao = mock()
	val configDao : ControllerConfigDao = mock()

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
		whenever(controllerManager.getControllerId()).thenReturn("TEST-CONTROLLER")
		whenever(assignmentDao.listByController(eq("TEST-CONTROLLER") ?: "")).thenReturn(assignments)
		whenever(hostDao.get(any(UUID::class.java) ?: host.id)).thenReturn(host)
		whenever(vmDao.get(any(UUID::class.java) ?: vm.id)).thenReturn(vm)
		whenever(vmDynDao.get(any(UUID::class.java) ?: vm.id)).thenReturn(vmDyn)

		val state = OperationalStateBuilderImpl(
				controllerManager,
				assignmentDao,
				hostDyn,
				hostCfg,
				hostDao,
				vStorageDao,
				vStorageDynDao,
				vmDao,
				vmDynDao,
				configDao).buildState()

		Assert.assertTrue(state.hosts.isNotEmpty())
		Assert.assertTrue(state.vms.isNotEmpty())
		Assert.assertTrue(state.vms.any { it.value.dynamic != null })

		Mockito.verify(controllerManager).getControllerId()
		Mockito.verify(assignmentDao).listByController(eq("TEST-CONTROLLER") ?: "")
	}
}