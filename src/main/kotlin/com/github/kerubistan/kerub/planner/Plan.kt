package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.join

data class Plan(
		val states: List<OperationalState>,
		val steps: List<AbstractOperationalStep> = listOf()
) {

	constructor(state: OperationalState, steps: List<AbstractOperationalStep> = listOf()) : this(listOf(state), steps)

	val state: OperationalState
		get() = states.last()

	fun reservations(): Collection<Reservation<*>> =
			steps.map { it.reservations() }.join().toSet()
}