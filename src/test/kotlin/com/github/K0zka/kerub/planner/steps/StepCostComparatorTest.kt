package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.costs.Risk
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class StepCostComparatorTest {

	val step1 : AbstractOperationalStep = mock()

	val step2 : AbstractOperationalStep = mock()

	@Test
	fun compareNoCosts() {
		Mockito.`when`(step1.getCost()).thenReturn(
				listOf()
		                                            )
		Mockito.`when`(step2.getCost()).thenReturn(
				listOf()
		                                            )

		Assert.assertEquals(StepCostComparator.compare(step1, step2), 0)
	}

	@Test
	fun compareRisk() {
		Mockito.`when`(step1.getCost()).thenReturn(
				listOf(
						Risk(score = 1, comment = "easy")
				      )
		                                            )
		Mockito.`when`(step2.getCost()).thenReturn(
				listOf(
						Risk(score = 2, comment = "hard")
				      )
		                                            )

		Assert.assertTrue(StepCostComparator.compare(step1, step2) < 0)
	}

	@Test
	fun compareRiskSort(){
		Mockito.`when`(step1.getCost()).thenReturn(
				listOf(
						Risk(score = 1, comment = "easy")
				      )
		                                            )
		Mockito.`when`(step2.getCost()).thenReturn(
				listOf(
						Risk(score = 2, comment = "hard")
				      )
		                                            )
		val original = listOf(step1, step2)
	}

}