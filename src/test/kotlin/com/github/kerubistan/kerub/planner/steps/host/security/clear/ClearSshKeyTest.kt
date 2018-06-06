package com.github.kerubistan.kerub.planner.steps.host.security.clear

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHost
import com.github.kerubistan.kerub.planner.steps.host.security.generate.GenerateSshKey
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ClearSshKeyTest {

	@Test
	fun take() {
		assertTrue("should remove the public key") {
			ClearSshKey(host = testHost).take(OperationalState.fromLists(
					hosts = listOf(testHost),
					hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
					hostCfgs = listOf(HostConfiguration(id = testHost.id, publicKey = "TEST"))
			)).let {
				it.hosts[testHost.id]!!.config!!.publicKey == null
			}
		}
	}

	@Test
	fun isInverseOf() {
		assertTrue("clear ssh key is inverse of generate") {
			ClearSshKey(host = testHost).isInverseOf(GenerateSshKey(host = testHost))
		}
		assertFalse("but only if the same host") {
			ClearSshKey(host = testHost).isInverseOf(GenerateSshKey(host = testFreeBsdHost))
		}
		assertFalse("and not inverse of any other steps") {
			ClearSshKey(host = testHost).isInverseOf(PowerDownHost(host = testHost))
		}
	}
}