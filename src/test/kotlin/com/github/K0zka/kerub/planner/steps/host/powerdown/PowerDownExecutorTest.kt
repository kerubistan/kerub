package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.host.PowerManager
import com.github.K0zka.kerub.model.Host
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class PowerDownExecutorTest {

	val hostManager: HostManager = mock()

	val powerManager: PowerManager = mock()

	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	@Test
	fun execute() {
		whenever(hostManager.getPowerManager(any<Host>()))
				.thenReturn(powerManager)

		PowerDownExecutor(hostManager).execute(PowerDownHost(host))

		verify(powerManager).off()
	}
}