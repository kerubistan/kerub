package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.HostService
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class UnshareNfsTest {

	@Test
	fun take() {
		val state = UnshareNfs(directory = "/kerub", host = testHost).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostCfgs = listOf(
								HostConfiguration(id = testHost.id, services = listOf(NfsService("/kerub")))
						)
				)
		)

		assertTrue(state.hosts[testHost.id]!!.config!!.services == listOf<HostService>())
	}

}