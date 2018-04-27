package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.steps.vstorage.mount.MountNfs
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.test.assertEquals

class RationalizedFirstSolutionTerminationStrategyTest {

	@Test
	fun onSolution() {
		val rationalizer = mock<PlanRationalizer>()
		val strategy = RationalizedFirstSolutionTerminationStrategy(rationalizer)
		val rawPlan = Plan(states = listOf(), steps = listOf(
				MountNfs(host = testHost, directory = "/test", remoteDirectory = "/mnt", remoteHost = testHost)))
		val rationalizedPlan = Plan(states = listOf(), steps = listOf())
		whenever(rationalizer.rationalize(eq(rawPlan))).thenReturn(rationalizedPlan)

		//WHEN
		strategy.onSolution(rawPlan)

		//THEN
		assertEquals(rationalizedPlan, strategy.solution)
	}
}