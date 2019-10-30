package com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.UnshareNfs
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StartNfsDaemonTest : OperationalStepVerifications() {

	override val step = StartNfsDaemon(testHost)

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
		assertEquals(listOf(NfsDaemonService()), state.hosts.getValue(testHost.id).config!!.services)
	}
}