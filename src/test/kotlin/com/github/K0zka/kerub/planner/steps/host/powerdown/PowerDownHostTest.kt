package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.planner.OperationalState
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

class PowerDownHostTest {

	@Test
	fun take() {
		val host = Host(
				address = "host-1.example.com",
				dedicated = true,
				publicKey = ""
		)
		val state = OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(HostDynamic(
						id = host.id
				))
		)

		val transformed = PowerDownHost(host).take(state)

		Assert.assertFalse(transformed.hostDyns.containsKey(host.id))
	}
}