package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.join

data class Plan(
		val state: OperationalState,
		val steps: List<AbstractOperationalStep> = listOf()
) {
	fun reservations(): Collection<Reservation<*>> =
			steps.map { it.reservations() }.join().toSet()
}