package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

abstract class AbstractFactoryVerifications(val factory: AbstractOperationalStepFactory<*>) {
	@Test
	fun checkBlankState() {
		assertTrue("Blank state should produce no steps") {
			factory.produce(OperationalState.fromLists()).isEmpty()
		}
	}

	@Test
	fun getExpectationHints() {
		// while I surely know that it won't be null thanks to kotlin, I'd also like to know that it is implemented
		assertNotNull(factory.expectationHints)
	}

	@Test
	fun getProblemHints() {
		// for the same reason as getExpectationHints
		assertNotNull(factory.problemHints)
	}
}