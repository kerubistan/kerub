package com.github.kerubistan.kerub.planner.steps.host.security.remove

import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.host.security.install.InstallPublicKey
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class RemovePublicKeyTest {

	@Test
	fun isInverseOf() {
		assertTrue("remove public key is inverse operation of installing the same key on the same host") {
			RemovePublicKey(host = testHost, publicKey = "ssh-rsa AAAAlofasz", hostOfKey = testFreeBsdHost).isInverseOf(
					InstallPublicKey(targetHost = testHost, publicKey = "ssh-rsa AAAAlofasz", sourceHost = testFreeBsdHost)
			)
		}
	}

	@Test
	fun take() {
		assertTrue("basic behavior") {
			RemovePublicKey(host = testHost, hostOfKey = testFreeBsdHost, publicKey = "ssh-rsa AAAA").take(
					OperationalState.fromLists(
							hosts = listOf(testHost, testFreeBsdHost),
							hostDyns = listOf(
									HostDynamic(id = testHost.id, status = HostStatus.Up),
									HostDynamic(id = testFreeBsdHost.id, status = HostStatus.Up)
							),
							hostCfgs = listOf(
									HostConfiguration(id = testHost.id, acceptedPublicKeys = listOf("ssh-rsa AAAA"))
							)
					)
			).let {
				it.hosts[testHost.id]!!.config!!.acceptedPublicKeys == listOf<String>()
			}
		}
	}
}