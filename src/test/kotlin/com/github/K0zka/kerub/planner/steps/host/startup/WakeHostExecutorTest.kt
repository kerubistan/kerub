package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.lom.WakeOnLanInfo
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WakeHostExecutorTest {
	val hostManager : HostManager = mock()

	val hostDynDao : HostDynamicDao = mock()

	val host = testHost.copy(
			capabilities = testHostCapabilities.copy(
				powerManagment = listOf(
						WakeOnLanInfo(listOf(ByteArray(6)))
				)
			)
	)

	@Test
	fun execute() {

		WakeHostExecutor(hostManager, hostDynDao).execute(WolWakeHost(host))

		Mockito.verify(hostDynDao).update(any(), any())
	}
}