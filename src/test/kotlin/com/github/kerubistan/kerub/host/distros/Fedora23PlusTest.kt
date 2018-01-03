package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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