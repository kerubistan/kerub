package com.github.kerubistan.kerub.planner.steps.host.security.install

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHost
import com.github.kerubistan.kerub.planner.steps.host.security.remove.RemovePublicKey
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InstallPublicKeyTest : OperationalStepVerifications() {
	override val step = RemovePublicKey(host = testHost, publicKey = "TEST", hostOfKey = testFreeBsdHost)

	@Test
	fun isInverseOf() {
		assertTrue("Install public key is inverse of remove the same public key") {
			InstallPublicKey(publicKey = "TEST", sourceHost = testFreeBsdHost, targetHost = testHost).isInverseOf(
					RemovePublicKey(host = testHost, publicKey = "TEST", hostOfKey = testFreeBsdHost)
			)
		}
		assertFalse("Install public key is inverse only if the same host") {
			InstallPublicKey(publicKey = "TEST", sourceHost = testFreeBsdHost, targetHost = testHost).isInverseOf(
					RemovePublicKey(host = testFreeBsdHost, publicKey = "TEST", hostOfKey = testHost)
			)
		}
		assertFalse("Install public key is inverse only if the same public key") {
			InstallPublicKey(publicKey = "TEST", targetHost = testHost, sourceHost = testFreeBsdHost).isInverseOf(
					RemovePublicKey(host = testHost, publicKey = "SOMETHING-ELSE-BUT-STILL-JUST_TEST", hostOfKey = testFreeBsdHost)
			)
		}
		assertFalse("Install public key is not inverse of anything else than install key") {
			InstallPublicKey(publicKey = "TEST", targetHost = testHost, sourceHost = testFreeBsdHost).isInverseOf(
					PowerDownHost(host = testHost)
			)
		}
	}

	@Test
	fun take() {
		assertTrue("") {
			val publicKey = "ssh-rsa AAAAA-whatever-it-is-just-a-test"
			InstallPublicKey(targetHost = testHost, publicKey = publicKey, sourceHost = testFreeBsdHost)
					.take(
							OperationalState.fromLists(
									hosts = listOf(testHost)
							)
					).let { publicKey in it.hosts.getValue(testHost.id).config!!.acceptedPublicKeys }
		}
	}
}