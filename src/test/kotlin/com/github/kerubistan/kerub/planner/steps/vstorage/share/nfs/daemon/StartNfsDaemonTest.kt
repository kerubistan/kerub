package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.UnshareNfs
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class StartNfsDaemonTest {

	@Test
	fun isInverseOf() {
		assertTrue("find inverse step") {
			val start = StartNfsDaemon(host = testHost)
			val stop = StopNfsDaemon(host = testHost)
			val unshare = UnshareNfs(host = testHost, directory = "/test")
			listOf(unshare, stop).find { start.isInverseOf(it) } == stop
		}
	}

	@Test
	fun take() {
		val state = StartNfsDaemon(testHost).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostCfgs = listOf(HostConfiguration(id = testHost.id))
				)
		)
		assertTrue(state.hosts[testHost.id]!!.config!!.services == listOf(NfsDaemonService()))
	}
}