package com.github.kerubistan.kerub.planner.steps.host.security.install

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class InstallPublicKeyFactoryTest {

	@Test
	fun produce() {
		assertTrue("blank state should produce no steps") {
			InstallPublicKeyFactory.produce(OperationalState.fromLists()).isEmpty()
		}
		assertTrue("both host up, source has the key, target does not have it") {
			InstallPublicKeyFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost, testFreeBsdHost),
							hostCfgs = listOf(
									HostConfiguration(id = testHost.id, publicKey = "ssh-rsa AAAAATEST"),
									HostConfiguration(id = testFreeBsdHost.id, acceptedPublicKeys = listOf())
							),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up),
									HostDynamic(id = testFreeBsdHost.id, status = HostStatus.Up)
							)
					)
			) == listOf(InstallPublicKey(sourceHost = testHost, targetHost = testFreeBsdHost, publicKey = "ssh-rsa AAAAATEST"))
		}
	}
}