package com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.HostService
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import org.junit.Assert.assertTrue
import org.junit.Test

class StopNfsDaemonTest : OperationalStepVerifications() {

	override val step = StopNfsDaemon(testHost)

	@Test
	fun take() {
		val state = StopNfsDaemon(testHost).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostCfgs = listOf(HostConfiguration(id = testHost.id, services = listOf(NfsDaemonService())))
				)
		)
		assertTrue(state.hosts.getValue(testHost.id).config!!.services == listOf<HostService>())
	}
}