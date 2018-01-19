package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

data class Plan(
		val state: OperationalState,
		val steps: List<AbstractOperationalStep> = listOf()
) {
	fun reservations(): Collection<Reservation<*>> {
		var result = setOf<Reservation<*>>()
		steps.forEach {
			result += it.reservations()
		}
		return result
	}
}