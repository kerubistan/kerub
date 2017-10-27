package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.data.AssignmentDao
import com.github.kerubistan.kerub.data.ControllerConfigDao
import com.github.kerubistan.kerub.data.DaoOperations
import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.ControllerManager
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.controller.Assignment
import com.github.kerubistan.kerub.model.controller.AssignmentType
import java.util.UUID

class OperationalStateBuilderImpl(
		private val controllerManager: ControllerManager,
		private val assignments: AssignmentDao,
		private val hostDyn: HostDynamicDao,
		private val hostCfg: HostConfigurationDao,
		private val hostDao: HostDao,
		private val virtualStorageDao: VirtualStorageDeviceDao,
		private val virtualStorageDynDao: VirtualStorageDeviceDynamicDao,
		private val vmDao: VirtualMachineDao,
		private val vmDynDao: VirtualMachineDynamicDao,
		private val configDao: ControllerConfigDao
) : OperationalStateBuilder {

	companion object {
		fun assignmentsOfType(assignments: List<Assignment>, type: AssignmentType): List<UUID>
				= assignments.filter { it.type == type }.map { it.entityId }

		//some parallelism would be definietly welcome here
		fun <T : Entity<UUID>> retrieveAll(assignments: List<UUID>, dao: DaoOperations.Read<T, UUID>): List<T>
				= assignments.map { dao.get(it) }.filterNot { it == null } as List<T>
	}

	override fun buildState(): OperationalState {
		val assignments = assignments.listByController(controllerManager.getControllerId())
		val hostAssignments = assignmentsOfType(assignments, AssignmentType.host)
		val vmAssignments = assignmentsOfType(assignments, AssignmentType.vm)
		val vstorageAssignments = assignmentsOfType(assignments, AssignmentType.vstorage)
		return OperationalState.fromLists(
				hosts = retrieveAll(hostAssignments, hostDao),
				hostDyns = retrieveAll(hostAssignments, hostDyn),
				hostCfgs = retrieveAll(hostAssignments, hostCfg),
				vms = retrieveAll(vmAssignments, vmDao),
				vmDyns = retrieveAll(vmAssignments, vmDynDao),
				vStorage = retrieveAll(vstorageAssignments, virtualStorageDao),
				vStorageDyns = retrieveAll(vstorageAssignments, virtualStorageDynDao)
		)
	}
}