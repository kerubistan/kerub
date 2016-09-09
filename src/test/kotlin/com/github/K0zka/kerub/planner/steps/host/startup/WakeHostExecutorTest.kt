package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.host.PowerManager
import com.github.K0zka.kerub.model.Host
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WakeHostExecutorTest {
	val hostManager : HostManager = mock()

	val powerManager: PowerManager = mock()

	val hostDynDao : HostDynamicDao = mock()

	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	@Test
	fun execute() {
		Mockito.`when`(hostManager.getPowerManager( Mockito.any(Host::class.java) ?: host ))
				.thenReturn(powerManager)

		WakeHostExecutor(hostManager, hostDynDao).execute(WakeHost(host))

		Mockito.verify(powerManager).on()
	}
}