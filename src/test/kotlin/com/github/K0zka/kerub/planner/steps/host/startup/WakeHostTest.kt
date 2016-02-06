package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import org.junit.Assert
import org.junit.Test

class WakeHostTest {
	@Test fun take() {
		val host = Host(
				address = "host-1.example.com",
				dedicated = true,
				publicKey = "test"
		               )
		val originalState = OperationalState.fromLists(
				hosts = listOf(host)
		                                              )

		val transformed = WakeHost(host).take(originalState)

		Assert.assertTrue(transformed.hostDyns.containsKey(host.id))
		Assert.assertTrue(transformed.hostDyns[host.id]?.status == HostStatus.Up)
	}
}