package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.lom.WakeOnLanInfo
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.mockito.Mockito

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
		val executor = spy(WakeHostExecutor(hostManager, hostDynDao))
		val step = WolWakeHost(host)
		doNothing().whenever(executor).wakeOnLoan(eq(host))
		executor.execute(step)

		Mockito.verify(hostDynDao).update(any(), any())
	}
}