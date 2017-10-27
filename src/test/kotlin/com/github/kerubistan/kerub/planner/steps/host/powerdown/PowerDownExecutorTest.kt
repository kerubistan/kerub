package com.github.kerubistan.kerub.planner.steps.host.powerdown

import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.Host
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class PowerDownExecutorTest {

	val hostManager: HostManager = mock()

	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	@Test
	fun execute() {
		PowerDownExecutor(hostManager).execute(PowerDownHost(host))

		verify(hostManager).powerDown(eq(host))
	}
}