package com.github.K0zka.kerub.planner.steps.host.recycle

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.testHost
import org.junit.Assert.assertTrue
import org.junit.Test

class RecycleHostTest {
	@Test
	fun take() {
		val host = testHost.copy(
				recycling = true
		)
		val state = RecycleHost(host).take(OperationalState.fromLists(
				hosts = listOf(host)
		))
		assertTrue(state.hosts.isEmpty())
	}

}