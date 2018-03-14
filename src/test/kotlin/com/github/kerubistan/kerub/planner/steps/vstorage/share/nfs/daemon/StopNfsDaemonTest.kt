package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.HostService
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import org.junit.Assert.assertTrue
import org.junit.Test

class StopNfsDaemonTest {
	@Test
	fun take() {
		val state = StopNfsDaemon(testHost).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostCfgs = listOf(HostConfiguration(id = testHost.id, services = listOf(NfsDaemonService())))
				)
		)
		assertTrue(state.hosts[testHost.id]!!.config!!.services == listOf<HostService>())
	}
}