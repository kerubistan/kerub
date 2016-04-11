package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FedoraTest {

	@Mock
	var session : ClientSession? = null

	@Test
	fun getPackageManager() {
		assertNotNull(Fedora().getPackageManager(session!!))
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