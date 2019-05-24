package com.github.kerubistan.kerub.planner.steps.host.startup

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.mockito.Mockito

class WakeHostExecutorTest {
	private val hostManager : HostManager = mock()

	private val hostDynDao : HostDynamicDao = mock()

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

		Mockito.verify(hostDynDao).update(any(), any(), any())
	}
}