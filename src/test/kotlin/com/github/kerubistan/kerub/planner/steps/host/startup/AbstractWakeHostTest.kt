package com.github.kerubistan.kerub.planner.steps.host.startup

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AbstractWakeHostTest {
	@Test fun take() {
		val host = Host(
				address = "host-1.example.com",
				dedicated = true,
				publicKey = "test"
		)
		val originalState = OperationalState.fromLists(
				hosts = listOf(host)
		)

		val transformed = WolWakeHost(host).take(originalState)

		assertNotNull(transformed.hosts[host.id]?.dynamic)
		assertEquals(HostStatus.Up, transformed.hosts[host.id]?.dynamic?.status)
	}
}