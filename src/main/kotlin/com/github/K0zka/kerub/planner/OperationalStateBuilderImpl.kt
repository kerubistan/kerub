package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.ControllerManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic

public class OperationalStateBuilderImpl(
		private val controllerManager: ControllerManager,
		private val assignments: AssignmentDao,
		private val hostDyn: HostDynamicDao,
		private val hostDao: HostDao
                                        ) : OperationalStateBuilder {
	override fun buildState(): OperationalState {
		val assignments = assignments.listByController(controllerManager.getControllerId())
		return OperationalState(
				hosts = assignments
						.map { hostDao.get(it.hostId) }
						.filter { it != null } as List<Host>,
				hostDyns = assignments
						.map { hostDyn.get(it.hostId) }
						.filter { it != null } as List<HostDynamic>,
				vmDyns = listOf(),
				vms = listOf()
		                       )
	}
}