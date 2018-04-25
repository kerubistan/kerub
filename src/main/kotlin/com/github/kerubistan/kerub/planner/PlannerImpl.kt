package com.github.kerubistan.kerub.planner

import com.github.k0zka.finder4j.backtrack.BacktrackService
import com.github.k0zka.finder4j.backtrack.termination.FirstSolutionTerminationStrategy
import com.github.k0zka.finder4j.backtrack.termination.OrTerminationStrategy
import com.github.k0zka.finder4j.backtrack.termination.TimeoutTerminationStrategy
import com.github.kerubistan.kerub.model.messages.Message
import com.github.kerubistan.kerub.model.messages.PingMessage
import com.github.kerubistan.kerub.planner.issues.problems.CompositeProblemDetectorImpl
import com.github.kerubistan.kerub.planner.reservations.FullHostReservation
import com.github.kerubistan.kerub.planner.reservations.HostMemoryReservation
import com.github.kerubistan.kerub.planner.reservations.HostReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.reservations.VmReservation
import com.github.kerubistan.kerub.planner.steps.CompositeStepFactory
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.now
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
		private val builder: OperationalStateBuilder,
		private val violationDetector : PlanViolationDetector
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
				val now = now()
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
			lastRun = now()
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
		val stepFactory = CompositeStepFactory(violationDetector)

		class Listener : FirstSolutionTerminationStrategy<Plan>() {
			override fun onSolution(state: Plan) {
				super.onSolution(
						PlanRationalizerImpl(problemDetector = CompositeProblemDetectorImpl, stepFactory = stepFactory,
											 violationDetector = violationDetector).rationalize(state))
			}
		}
		val listener = Listener()
		val strategy = OrTerminationStrategy<Plan>(listOf(
				listener,
				TimeoutTerminationStrategy(now() + 2000)
		)
		)
		logger.debug("starting planing")
		logger.debug("reservations: " + state.reservations)

		backtrack.backtrack(
				state = Plan(states = listOf(state)),
				factory = stepFactory,
				terminationStrategy = strategy,
				listener = listener,
				check = {
					violationDetector.listViolations(it).isEmpty()
							//well, at least...
							&& CompositeProblemDetectorImpl.detect(it).isEmpty()
				}
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