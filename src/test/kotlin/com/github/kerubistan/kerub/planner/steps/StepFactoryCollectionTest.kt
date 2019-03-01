package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.planner.OperationalState
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

class StepFactoryCollectionTest {

	private val factory1 = mock<AbstractOperationalStepFactory<*>>()
	private val factory2 = mock<AbstractOperationalStepFactory<*>>()
	private val step1 = mock<AbstractOperationalStep>()
	private val step2 = mock<AbstractOperationalStep>()

	@Test
	fun produceEmpty() {
		assertTrue(StepFactoryCollection(listOf()).produce(OperationalState.fromLists()).isEmpty())
	}

	@Test
	fun produce() {
		whenever(factory1.produce(
				Mockito.any(OperationalState::class.java)?: OperationalState.fromLists() )).thenReturn(listOf(step1))
		whenever(factory2.produce(
				Mockito.any(OperationalState::class.java)?: OperationalState.fromLists() )).thenReturn(listOf(step2))
		val steps = StepFactoryCollection(listOf(factory1, factory2)).produce(OperationalState.fromLists())
		assertEquals(2, steps.size)
		assertTrue(steps.contains(step1))
		assertTrue(steps.contains(step2))
	}

	@Test
	fun produceWithDisabledFeature() {
		whenever(factory1.produce(
				Mockito.any(OperationalState::class.java) ?: OperationalState.fromLists())).thenReturn(listOf(step1))
		whenever(factory2.produce(
				Mockito.any(OperationalState::class.java) ?: OperationalState.fromLists())).thenReturn(listOf(step2))

		val steps = StepFactoryCollection(listOf(factory1, factory2)) { false }.produce(OperationalState.fromLists())

		assertEquals(steps, listOf<AbstractOperationalStep>())
	}


}