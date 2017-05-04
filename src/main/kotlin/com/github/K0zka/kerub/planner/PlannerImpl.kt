package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.messages.Message
import com.github.K0zka.kerub.model.messages.PingMessage
import com.github.K0zka.kerub.planner.reservations.FullHostReservation
import com.github.K0zka.kerub.planner.reservations.HostMemoryReservation
import com.github.K0zka.kerub.planner.reservations.HostReservation
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.CompositeStepFactory
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.join
import com.github.k0zka.finder4j.backtrack.BacktrackService
import com.github.k0zka.finder4j.backtrack.termination.FirstSolutionTerminationStrategy
import com.github.k0zka.finder4j.backtrack.termination.OrTerminationStrategy
import com.github.k0zka.finder4j.backtrack.termination.TimeoutTerminationStrategy
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.scheduleAtFixedRate

/**
 * Implementation of planner.
 * <strong> This implementation stores mutable state.</strong>
 */
class PlannerImpl(
		private val backtrack: BacktrackService,
		private val executor: PlanExecutor,
		private val builder: OperationalStateBuilder
) : Planner {

	private val timer = Timer()
	var timerTask: TimerTask? = null
	private val reservations = ConcurrentHashMap<Plan, List<Reservation<*>>>()
	@Volatile var inProgress: Boolean = false
	@Volatile var lastRun: Long? = null

	companion object {
		fun checkReservations(planReservations: Collection<Reservation<*>>, reservations: List<Reservation<*>>): Boolean =
				planReservations.all {
					checkReservation(it, reservations)
				}

		/**
		 * Return true if the requested reservation is not in collission with the existing list of reservations.
		 */
		fun checkReservation(requestedReservation: Reservation<*>, reservations: List<Reservation<*>>): Boolean {
			when (requestedReservation) {
				is FullHostReservation -> {
					return reservations.none {
						reservation ->
						reservation is HostReservation && reservation.host == requestedReservation.host
					}
				}
				is VmReservation -> {
					return !reservations.contains(requestedReservation)
				}
				is UseHostReservation -> {
					return !reservations.contains(requestedReservation)
				}
				is VirtualStorageReservation -> {
					return !reservations.contains(requestedReservation)
				}
				is HostMemoryReservation -> {
					return true //TODO: correct verification needed
				}
				else -> TODO("check not implemented: ${requestedReservation}")
			}
		}

		private val logger = getLogger(PlannerImpl::class)
	}

	fun start() {
		val delay = 1000L
		synchronized(this) {
			timerTask = timer.scheduleAtFixedRate(delay, delay) {
				val now = System.currentTimeMillis()
				if (!inProgress && lastRun ?: 0 < (now - delay)) {
					onEvent(PingMessage(now))
				}
			}
		}
	}

	fun stop() {
		synchronized(this) {
			timerTask?.cancel()
		}
	}

	override fun onEvent(msg: Message) {
		val state = buildState()
		try {
			inProgress = true
			lastRun = System.currentTimeMillis()
			plan(state)
		} finally {
			inProgress = false
		}

	}

	private fun buildState(): OperationalState {
		return builder.buildState().copy(
				reservations = reservations.values.join()
		)
	}

	private fun plan(state: OperationalState) {
		val listener = FirstSolutionTerminationStrategy<Plan, AbstractOperationalStep>()
		val strategy = OrTerminationStrategy<Plan>(listOf(
				listener,
				TimeoutTerminationStrategy(System.currentTimeMillis() + 20000)
		)
		)
		logger.debug("starting planing")
		logger.debug("reservations: " + state.reservations)

		backtrack.backtrack(
				Plan(
						state = state
				),
				CompositeStepFactory,
				listener,
				listener
		)
		val plan = listener.solution
		if (plan == null) {
			logger.debug("No plan generated.")
		} else {
			val planReservations = plan.reservations()
			if (checkReservations(planReservations, reservations.values.join())) {
				reservations.put(plan, planReservations.toList())
				executor.execute(plan, {
					reservations.remove(plan)
				})
			} else {
				logger.info("reservations not matched")
			}
		}
	}

}