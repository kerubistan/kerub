package com.github.kerubistan.kerub.planner.steps.host.security.remove

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class RemovePublicKeyFactoryTest {

	@Test
	fun produce() {
		assertTrue("blank state - no steps") {
			RemovePublicKeyFactory.produce(OperationalState.fromLists()).isEmpty()
		}
		assertTrue("single host - no steps") {
			RemovePublicKeyFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up)
							)
					)
			).isEmpty()
		}
		assertTrue("two hosts, installed keys") {
			RemovePublicKeyFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost, testFreeBsdHost),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up),
									HostDynamic(id = testFreeBsdHost.id, status = HostStatus.Up)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											publicKey = "TEST"
									),
									HostConfiguration(
											id = testFreeBsdHost.id,
											acceptedPublicKeys = listOf("TEST")
									)
							)
					)
			) == listOf(RemovePublicKey(hostOfKey = testHost, publicKey = "TEST", host = testFreeBsdHost))
		}
	}
}