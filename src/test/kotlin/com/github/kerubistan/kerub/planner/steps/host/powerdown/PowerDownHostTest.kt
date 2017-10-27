package com.github.kerubistan.kerub.planner.steps.host.powerdown

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Assert.assertNull
import org.junit.Test

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

		assertNull(transformed.hosts[host.id]?.dynamic)
	}
}