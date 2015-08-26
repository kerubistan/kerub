package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.ControllerManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.messages.EntityMessage
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.CompositeStepFactory
import com.github.K0zka.kerub.utils.getLogger
import com.github.k0zka.finder4j.backtrack.BacktrackService
import com.github.k0zka.finder4j.backtrack.termination.FirstSolutionTerminationStrategy

public class PlannerImpl(
		private val backtrack : BacktrackService,
		private val executor: PlanExecutor,
		private val controllerManager: ControllerManager,
		private val assignments: AssignmentDao,
		private val hostDyn: HostDynamicDao,
		private val hostDao: HostDao
                        ) : Planner {
	companion object {
		val logger = getLogger(PlannerImpl::class)
	}

	internal fun buildOperationalState(): OperationalState {
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

	override fun onEvent(msg: EntityMessage) {
		val strategy = FirstSolutionTerminationStrategy<OperationalState, AbstractOperationalStep>()

		backtrack.backtrack(
				buildOperationalState(),
				CompositeStepFactory,
				strategy,
		        strategy
				)
		val solution = strategy.getSolution();

	}
}