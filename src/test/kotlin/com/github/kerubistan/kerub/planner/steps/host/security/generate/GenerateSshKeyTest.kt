package com.github.kerubistan.kerub.planner.steps.host.security.generate

import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHost
import com.github.kerubistan.kerub.planner.steps.host.security.clear.ClearSshKey
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GenerateSshKeyTest {

	@Test
	fun isInverseOf() {
		assertTrue("remove ssh key is the inverse of generating one") {
			GenerateSshKey(host = testHost).isInverseOf(ClearSshKey(host = testHost))
		}
		assertFalse("inverse of remove, but only on the same host") {
			GenerateSshKey(host = testHost).isInverseOf(ClearSshKey(host = testFreeBsdHost))
		}
		assertFalse("generate ssh key is not inverse of steps other than clearing it") {
			GenerateSshKey(host = testHost).isInverseOf(PowerDownHost(testHost))
		}
	}

	@Test
	fun take() {
		assertTrue("") {
			GenerateSshKey(host = testHost).take(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up)
							)
					)
			).let {
				it.hosts.getValue(testHost.id).config!!.publicKey != null
			}
		}
	}
}