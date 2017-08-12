package com.github.K0zka.kerub.planner.steps.host.recycle

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.testHost
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID.randomUUID

class RecycleHostFactoryTest {
	@Test
	fun produce() {
		val hostToRemove = testHost.copy(
				id = randomUUID(),
				recycling = true
		)
		assertEquals(listOf(RecycleHost(hostToRemove)),
				RecycleHostFactory.produce(
						OperationalState.fromLists(
								hosts = listOf(hostToRemove)
						)
				)
		)
		assertEquals(listOf(RecycleHost(hostToRemove)),
				RecycleHostFactory.produce(
						OperationalState.fromLists(
								hosts = listOf(hostToRemove, testHost)
						)
				)
		)
		val notDedicatedHostToRemove = hostToRemove.copy(
				dedicated = false
		)
		assertEquals(listOf(RecycleHost(notDedicatedHostToRemove)),
				RecycleHostFactory.produce(
						OperationalState.fromLists(
								hosts = listOf(notDedicatedHostToRemove)
						)
				)
		)
	}

}