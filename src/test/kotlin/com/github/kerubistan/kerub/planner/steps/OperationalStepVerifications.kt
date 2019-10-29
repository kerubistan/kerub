package com.github.kerubistan.kerub.planner.steps

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kerubistan.kerub.utils.createObjectMapper
import org.junit.Test
import kotlin.test.assertEquals
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

	@Test
	fun jsonSerialization() {
		val objectMapper = createObjectMapper(prettyPrint = true)
		val json = objectMapper.writeValueAsString(step)
		val copy = objectMapper.readValue<AbstractOperationalStep>(json)
		val copyJson = objectMapper.writeValueAsString(copy)
		assertEquals(json, copyJson)

	}

}