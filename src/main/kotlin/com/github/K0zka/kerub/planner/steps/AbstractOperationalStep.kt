package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.model.Constrained
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.k0zka.finder4j.backtrack.Step

interface AbstractOperationalStep : Step<Plan> {

	/**
	 * Take an operational state transformation step
	 */
	fun take(state: OperationalState): OperationalState

	/**
	 *
	 */
	override fun take(state: Plan): Plan =
			Plan(
					state = take(state.state),
					steps = state.steps + this
			                              )

	/**
	 * Get the list of costs expected at executing this step.
	 * Default implementation returns an empty list, meaning negligible
	 * costs.
	 */
	open fun getCost(): List<Cost> = listOf();

	/**
	 * Returns a map of violated resources.
	 */
	open fun violations(state: OperationalState)
			: Map<Constrained<Expectation>, List<Expectation>>
			= mapOf()

	open fun reservations() = listOf<Reservation<*>>()
}