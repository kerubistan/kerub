package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class UnshareNfsTest {

	@Test
	fun isInverseOf() {
		assertTrue("find inverse step") {
			val share = ShareNfs(host = testHost, directory = "/test")
			val unshare = UnshareNfs(host = testHost, directory = "/test")
			listOf(share).find { it.isInverseOf(unshare) } == share
		}
	}

	@Test
	fun take() {
		val state = UnshareNfs(directory = "/kerub", host = testHost).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostCfgs = listOf(
								HostConfiguration(id = testHost.id, services = listOf(NfsService("/kerub", write = true)))
						)
				)
		)

		assertTrue(state.hosts.getValue(testHost.id).config!!.services.isEmpty())
	}

}