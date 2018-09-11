package com.github.kerubistan.kerub.planner.steps

import org.junit.Test
import org.reflections.Reflections
import kotlin.test.assertTrue

class StepsTest {
	@Test
	fun checkAllStepsAreData() {
		Reflections("com.github.kerubistan.kerub.planner.steps")
				.getSubTypesOf(AbstractOperationalStep::class.java).forEach {
					assertTrue("${it.name} must be data class") {
						it.isInterface || it.kotlin.isAbstract || it.kotlin.isData
					}
				}
	}
}