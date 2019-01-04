package com.github.kerubistan.kerub.planner.steps.host.security.remove

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class RemovePublicKeyFactoryTest : AbstractFactoryVerifications(RemovePublicKeyFactory) {

	@Test
	fun produce() {
		assertTrue("single host - no steps") {
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up)
							)
					)
			).isEmpty()
		}
		assertTrue("two hosts, installed keys") {
			factory.produce(
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