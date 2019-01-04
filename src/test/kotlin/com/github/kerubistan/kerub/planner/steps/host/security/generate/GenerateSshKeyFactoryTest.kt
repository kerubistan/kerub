package com.github.kerubistan.kerub.planner.steps.host.security.generate

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class GenerateSshKeyFactoryTest : AbstractFactoryVerifications(GenerateSshKeyFactory) {

	@Test
	fun produce() {
		assertTrue("single host without public key - offer to create") {
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up)
							)
					)
			) == listOf(GenerateSshKey(host = testHost))
		}
		assertTrue("single host without public key - but it does not run, do nothing") {
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost)
					)
			).isEmpty()
		}
		assertTrue("single host with public key - do nothing") {
			factory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = testHost.id,
											publicKey = "TEST"
									)
							)
					)
			).isEmpty()
		}
	}
}