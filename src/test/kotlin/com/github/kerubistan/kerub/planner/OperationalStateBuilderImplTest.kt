package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.MB
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
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import java.util.UUID

class OperationalStateBuilderImplTest {

	private val controllerManager: ControllerManager = mock()
	private val assignmentDao: AssignmentDao = mock()
	private val hostDyn: HostDynamicDao = mock()
	private val hostCfg: HostConfigurationDao = mock()
	private val hostDao: HostDao = mock()
	private val vmDao : VirtualMachineDao = mock()
	private val vmDynDao : VirtualMachineDynamicDao = mock()
	private val vStorageDao : VirtualStorageDeviceDao = mock()
	private val vStorageDynDao: VirtualStorageDeviceDynamicDao = mock()
	private val configDao : ControllerConfigDao = mock()

	private val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	private val vm = VirtualMachine(
			id = UUID.randomUUID(),
			name = "vm-1"
	)

	private val vmDyn = VirtualMachineDynamic(
			id = vm.id,
			hostId = host.id,
			status = VirtualMachineStatus.Up,
			memoryUsed = 256.MB
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
		whenever(configDao.get()).thenReturn(ControllerConfig())  // just default configuration
		whenever(controllerManager.getControllerId()).thenReturn("TEST-CONTROLLER")
		whenever(assignmentDao.listByController(eq("TEST-CONTROLLER") ?: "")).thenReturn(assignments)
		whenever(hostDao[any(UUID::class.java) ?: host.id]).thenReturn(host)
		whenever(vmDao[any(UUID::class.java) ?: vm.id]).thenReturn(vm)
		whenever(vmDynDao[any(UUID::class.java) ?: vm.id]).thenReturn(vmDyn)

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