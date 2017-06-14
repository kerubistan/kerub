package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
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