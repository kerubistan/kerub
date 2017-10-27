package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.reservations.FullHostReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import java.util.UUID

class PlanTest {

	@Test
	fun reservationsWithNoSteps() {
		assertEquals(setOf<Reservation<*>>(), Plan(
				OperationalState.fromLists(),
				listOf()
		).reservations())
	}

	@Test
	fun reservationsWithSameHost() {

		val host = Host(
				id = UUID.randomUUID(),
				dedicated = true,
				publicKey = "",
				address = "host1.example.com"
		)
		val step1 = Mockito.mock(AbstractOperationalStep::class.java)
		val step2 = Mockito.mock(AbstractOperationalStep::class.java)
		Mockito.`when`(step1.reservations()).thenReturn(listOf(FullHostReservation(host)))
		Mockito.`when`(step2.reservations()).thenReturn(listOf(FullHostReservation(host)))

		assertEquals(setOf<Reservation<*>>(FullHostReservation(host)), Plan(
				OperationalState.fromLists(),
				listOf(step1, step2)
		).reservations())
	}

}