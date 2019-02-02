package com.github.kerubistan.kerub.planner.steps

import org.junit.Test
import kotlin.test.assertTrue

abstract class OperationalStepVerifications {

	abstract val step : AbstractOperationalStep

	@Test
	fun costs() {
		assertTrue("costs function should be implemented, even if it returns empty list") {
			step.getCost().any() || step.getCost().none()
		}
	}

	@Test
	fun reservations() {
		assertTrue("reservations function should be implemented, even if it returns empty list") {
			step.reservations().any() || step.reservations().none()
		}

	}

}