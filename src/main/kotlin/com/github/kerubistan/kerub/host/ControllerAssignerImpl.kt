package com.github.kerubistan.kerub.host

import com.github.k0zka.finder4j.backtrack.BacktrackService
import com.github.k0zka.finder4j.backtrack.State
import com.github.k0zka.finder4j.backtrack.Step
import com.github.k0zka.finder4j.backtrack.StepFactory
import com.github.k0zka.finder4j.backtrack.termination.FirstSolutionTerminationStrategy
import com.github.kerubistan.kerub.controller.HostAssignedMessage
import com.github.kerubistan.kerub.controller.InterController
import com.github.kerubistan.kerub.data.AssignmentDao
import com.github.kerubistan.kerub.data.ControllerDao
import com.github.kerubistan.kerub.data.dynamic.ControllerDynamicDao
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.controller.Assignment
import com.github.kerubistan.kerub.model.controller.AssignmentType
import com.github.kerubistan.kerub.model.dynamic.ControllerDynamic
import com.github.kerubistan.kerub.utils.getLogger
import java.util.HashMap

/*
 * This is considered a placeholder for the logic that assigns hosts to controllers.
 * The final implementation should consider network location, controller capacity,
 * host capabilities and the workload of the controller
 */
class ControllerAssignerImpl(private val backtrack: BacktrackService,
							 private val controllerDao: ControllerDao,
							 private val controllerDynamicDao: ControllerDynamicDao,
							 private val hostAssignmentDao: AssignmentDao,
							 private val interController: InterController) : ControllerAssigner {

	data class ControllerAssignmentState(
			val hostsToAssign: List<Host>,
			val controllers: List<String>,
			val controllerStates: Map<String, ControllerDynamic>,
			val assignments: Map<Host, String>) : State {
		override val complete: Boolean
			get() = hostsToAssign.isEmpty()
	}

	data class ControllerAssignmentStep(val host: Host, val controller: String) : Step<ControllerAssignmentState> {
		override fun take(state: ControllerAssignmentState): ControllerAssignmentState {
			val assignments = HashMap<Host, String>(state.assignments)
			assignments[host] = controller

			val oldControllerState = state.controllerStates[controller] ?: ControllerDynamic(
					controllerId = controller,
					addresses = listOf(),
					maxHosts = 128,
					totalHosts = 0)
			val newControllerState = oldControllerState.copy(totalHosts = oldControllerState.totalHosts + 1)

			return ControllerAssignmentState(
					hostsToAssign = state.hostsToAssign.filter { it != host },
					controllers = state.controllers,
					controllerStates = state.controllerStates
							+ (controller to newControllerState),
					assignments = assignments)
		}
	}

	object ControllerAssignmentStepFactory : StepFactory<ControllerAssignmentStep, ControllerAssignmentState> {
		override fun produce(state: ControllerAssignmentState): List<ControllerAssignmentStep> {
			val host = state.hostsToAssign.firstOrNull()
			return if (host == null) {
				listOf()
			} else {
				state.controllers.filter {
					//TODO: filter out overloaded controllers (is this needed at all?)
					true
				}
						.map { ControllerAssignmentStep(host, it) }
						.sortedBy { step: ControllerAssignmentStep ->
							controllerScore(state.controllerStates[step.controller])
						}
			}
		}
	}

	companion object {
		private val logger = getLogger(ControllerAssignerImpl::class)
		fun controllerScore(state: ControllerDynamic?): Int =
			if (state == null) {
				-1
			} else {
				state.maxHosts - state.totalHosts
			}
	}

	override fun assignControllers(hosts: List<Host>) {
		logger.info("Searching host-controller assignment for host {}", hosts)
		val strategy = FirstSolutionTerminationStrategy<ControllerAssignmentState>()
		val controllerList = controllerDynamicDao.listAll()
		backtrack.backtrack(
				ControllerAssignmentState(hosts,
						controllerList.map { it.controllerId },
						controllerList.associateBy { it.controllerId },
						mapOf()),
				ControllerAssignmentStepFactory,
				//this is not quite
				strategy,
				strategy
		)
		strategy.solution?.assignments?.forEach {
			assignment ->
			hostAssignmentDao.add(Assignment(
					entityId = assignment.key.id,
					controller = assignment.value,
					type = AssignmentType.host))
			interController.sendToController(assignment.value,
											 HostAssignedMessage(
													 hostId = assignment.key.id,
													 conrollerId = assignment.value
											 ))

		}
	}
}