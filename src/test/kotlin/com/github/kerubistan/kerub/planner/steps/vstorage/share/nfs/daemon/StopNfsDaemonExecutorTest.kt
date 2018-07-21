package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.host.ServiceManager
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.utils.junix.nfs.Exports
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class StopNfsDaemonExecutorTest {

	@Test
	fun execute() {
		val hostManager = mock<HostManager>()
		val hostConfigDao = mock<HostConfigurationDao>()
		val serviceManager = mock<ServiceManager>()

		whenever(hostManager.getServiceManager(eq(testHost))).thenReturn(serviceManager)

		StopNfsDaemonExecutor(hostManager, hostConfigDao).execute(StopNfsDaemon(testHost))
		verify(serviceManager).stop(eq(Exports))
	}
}