package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.k0zka.finder4j.backtrack.State

data class Plan(
		val state: OperationalState,
		val steps: List<AbstractOperationalStep> = listOf()
) : State {

	override fun isComplete(): Boolean = state.isComplete

	fun reservations(): Collection<Reservation<*>> {
		var result = setOf<Reservation<*>>()
		steps.forEach {
			result += it.reservations()
		}
		return result
	}
}