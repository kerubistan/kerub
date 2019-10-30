package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertTrue

class ShareNfsTest : OperationalStepVerifications() {

	override val step = ShareNfs(
			host = testHost,
			directory = "/kerub",
			write = true
	)

	@Test
	fun isInverseOf() {
		assertTrue("find inverse step") {
			val share = ShareNfs(host = testHost, directory = "/test")
			val unshare = UnshareNfs(host = testHost, directory = "/test")
			listOf(unshare).find { it.isInverseOf(share) } == unshare
		}
	}

	@Test
	fun take() {
		val state = ShareNfs(directory = "/kerub", host = testHost).take(
				OperationalState.fromLists(
						hosts = listOf(testHost)
				)
		)
		assertTrue(state.hosts.getValue(testHost.id).config!!.services.any { it is NfsService && it.directory == "/kerub" })
	}
}