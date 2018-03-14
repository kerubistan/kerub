package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import org.junit.Assert.assertTrue
import org.junit.Test

class ShareNfsTest {

	@Test
	fun take() {
		val state = ShareNfs(directory = "/kerub", host = testHost).take(
				OperationalState.fromLists(
						hosts = listOf(testHost)
				)
		)
		assertTrue(state.hosts[testHost.id]!!.config!!.services.any { it is NfsService && it.directory == "/kerub" })
	}
}