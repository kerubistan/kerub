package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.planner.costs.Risk
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StepCostComparatorTest {

	private val step1 : AbstractOperationalStep = mock()

	private val step2 : AbstractOperationalStep = mock()

	@Test
	fun compareNoCosts() {
		whenever(step1.getCost()).thenReturn(listOf())
		whenever(step2.getCost()).thenReturn(listOf())

		assertEquals(StepCostComparator.compare(step1, step2), 0)
	}

	@Test
	fun compareRisk() {
		whenever(step1.getCost()).thenReturn(listOf(Risk(score = 1, comment = "easy")))
		whenever(step2.getCost()).thenReturn(listOf(Risk(score = 2, comment = "hard")))

		assertTrue(StepCostComparator.compare(step1, step2) < 0)
	}

	@Test
	fun compareRiskSort(){
		whenever(step1.getCost()).thenReturn(listOf(Risk(score = 1, comment = "easy")))
		whenever(step2.getCost()).thenReturn(listOf(Risk(score = 2, comment = "hard")))
		val original = listOf(step1, step2)
		//TODO
	}

}