package com.github.kerubistan.kerub.planner.steps.host.recycle

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import org.junit.Assert.assertTrue
import org.junit.Test

class RecycleHostTest : OperationalStepVerifications() {
	override val step = RecycleHost(testHost.copy(recycling = true))

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