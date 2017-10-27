package com.github.kerubistan.kerub.planner.steps.host.powerdown

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Test
import kotlin.test.assertTrue

class PowerDownTest {

	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	val hostDyn = HostDynamic(
			id = host.id,
			status = HostStatus.Up
	)

	@Test
	fun take() {
		val state = PowerDownHost(host).take(OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(hostDyn)
		))

		assertTrue(state.hosts.all { it.value.dynamic == null })
	}
}