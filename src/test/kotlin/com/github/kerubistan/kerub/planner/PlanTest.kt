package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.reservations.FullHostReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHost
import com.github.kerubistan.kerub.testHost
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import java.util.UUID
import kotlin.test.assertTrue

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

	@Test
	fun planBy() {
		assertTrue("Empty plan generation") {
			Plan.planBy(OperationalState.fromLists(), listOf()) == Plan(OperationalState.fromLists(), listOf())
		}
		assertTrue("Single step") {
			val initial = OperationalState.fromLists(
					hosts = listOf(testHost),
					hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up))
			)
			Plan.planBy(
					initial,
					listOf(PowerDownHost(host = testHost))
			) == Plan(
					states = listOf(initial, OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf()
					)),
					steps = listOf(PowerDownHost(host = testHost))
			)
		}
	}

}