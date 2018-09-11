package com.github.kerubistan.kerub.planner.costs

import org.junit.Test
import org.reflections.Reflections
import kotlin.test.assertTrue

class CostTest {
	@Test
	fun checkAllCostsAreData() {
		Reflections("com.github.kerubistan.kerub")
				.getSubTypesOf(Cost::class.java).forEach {
					assertTrue("${it.name} must be data class") {
						it.isInterface || it.kotlin.isAbstract || it.kotlin.isData
					}
				}
	}
}