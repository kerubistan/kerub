package com.github.kerubistan.kerub.planner.steps.host.security.clear

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

internal class ClearSshKeyFactoryTest {

	@Test
	fun produce() {
		assertTrue("blank state should produce no steps") {
			ClearSshKeyFactory.produce(OperationalState.fromLists()).isEmpty()
		}
		assertTrue("Two hosts, one has public key, not installed on the other") {
			val host1 = testHost.copy(id = randomUUID())
			val host2 = testHost.copy(id = randomUUID())
			ClearSshKeyFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(
									HostDynamic(id = host1.id, status = HostStatus.Up)
							),
							hostCfgs = listOf(
									HostConfiguration(id = host1.id, publicKey = "TEST")
							)
					)
			) == listOf(ClearSshKey(host = host1))
		}
		assertTrue("Two hosts, one has public key, used by the other") {
			val host1 = testHost.copy(id = randomUUID())
			val host2 = testHost.copy(id = randomUUID())
			ClearSshKeyFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host1, host2),
							hostDyns = listOf(
									HostDynamic(id = host1.id, status = HostStatus.Up)
							),
							hostCfgs = listOf(
									HostConfiguration(id = host1.id, publicKey = "TEST"),
									HostConfiguration(id = host2.id, acceptedPublicKeys = listOf("TEST"))
							)
					)
			).isEmpty()
		}
	}
}