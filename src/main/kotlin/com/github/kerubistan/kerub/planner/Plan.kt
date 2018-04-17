package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.join

data class Plan(
		val states: List<OperationalState>,
		val steps: List<AbstractOperationalStep> = listOf()
) {

	companion object {
		fun planBy(initial: OperationalState, steps: List<AbstractOperationalStep>): Plan {
			var states = listOf(initial)
			steps.forEach { states += it.take(states.last()) }
			return Plan(
					steps = steps,
					states = states
			)
		}
	}

	constructor(state: OperationalState, steps: List<AbstractOperationalStep> = listOf()) : this(listOf(state), steps)

	val state: OperationalState
		get() = states.last()

	fun reservations(): Collection<Reservation<*>> =
			steps.map { it.reservations() }.join().toSet()
}