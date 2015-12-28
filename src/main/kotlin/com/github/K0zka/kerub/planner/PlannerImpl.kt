package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.messages.EntityMessage
import com.github.K0zka.kerub.planner.reservations.FullHostReservation
import com.github.K0zka.kerub.planner.reservations.HostReservation
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.CompositeStepFactory
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.join
import com.github.k0zka.finder4j.backtrack.BacktrackService
import com.github.k0zka.finder4j.backtrack.termination.FirstSolutionTerminationStrategy
import java.util.concurrent.ConcurrentHashMap

/**
 * Implementation of planner.
 * <strong> This implementation stores mutable state.</strong>
 */
public class PlannerImpl(
		private val backtrack: BacktrackService,
		private val executor: PlanExecutor,
		private val builder: OperationalStateBuilder
) : Planner {

	companion object {
		fun checkReservations(planReservations: Collection<Reservation<*>>, reservations: List<Reservation<*>>): Boolean =
				planReservations.any {
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
				is VmReservation       -> {
					return !reservations.contains(requestedReservation)
				}
				is UseHostReservation  -> {
					return !reservations.contains(requestedReservation)
				}
				else                   ->
					return false
			}
		}

		private val logger = getLogger(PlannerImpl::class)
	}

	private val reservations = ConcurrentHashMap<Plan, List<Reservation<*>>>()

	override fun onEvent(msg: EntityMessage) {
		val strategy = FirstSolutionTerminationStrategy<Plan, AbstractOperationalStep>()

		logger.debug("starting planing")

		backtrack.backtrack(
				Plan(
						state = builder.buildState().copy(
								reservations = reservations.values.join()
						)
				),
				CompositeStepFactory,
				strategy,
				strategy
		)
		val plan = strategy.getSolution()
		if (plan == null) {
			logger.debug("No plan generated.", msg)
		} else {
			val planReservations = plan.reservations()
			checkReservations(planReservations, reservations.values.join())
			reservations.put(plan, planReservations.toList())
			executor.execute(plan, {
				reservations.remove(plan)
			})
		}

	}

}