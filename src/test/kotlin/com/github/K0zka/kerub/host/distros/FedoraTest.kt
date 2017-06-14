package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FedoraTest {

	val session : ClientSession = mock()

	@Test
	fun getPackageManager() {
		assertNotNull(Fedora().getPackageManager(session))
	}

	@Test
	fun handlesVersion() {
		assertTrue(Fedora().handlesVersion(Version.fromVersionString("19")))
		assertTrue(Fedora().handlesVersion(Version.fromVersionString("20")))
		assertTrue(Fedora().handlesVersion(Version.fromVersionString("21")))
		assertTrue(Fedora().handlesVersion(Version.fromVersionString("22")))
		assertFalse(Fedora().handlesVersion(Version.fromVersionString("23")))
		assertFalse(Fedora().handlesVersion(Version.fromVersionString("blah")))
	}
}