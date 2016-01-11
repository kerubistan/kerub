package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version
import org.apache.sshd.ClientSession
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class Fedora23PlusTest {

	var session : ClientSession? = null

	@Test
	fun getPackageManager() {

	}

	@Test
	fun handlesVersion() {
		assertTrue(Fedora23Plus().handlesVersion(Version.fromVersionString("23")))
		assertTrue(Fedora23Plus().handlesVersion(Version.fromVersionString("24")))
		assertFalse(Fedora23Plus().handlesVersion(Version.fromVersionString("22")))
		assertFalse(Fedora23Plus().handlesVersion(Version.fromVersionString("blah")))
	}
}