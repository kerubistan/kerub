package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

class OpenSuseTest {

	@Test
	fun testGetPackageManager() {
		assertNotNull(OpenSuse().getPackageManager(Mockito.mock(ClientSession::class.java)))
	}

	@Test
	fun testHandlesVersion() {
		assertTrue((OpenSuse().handlesVersion(Version.fromVersionString("42"))))
		assertTrue((OpenSuse().handlesVersion(Version.fromVersionString("42.2"))))
		assertTrue((OpenSuse().handlesVersion(Version.fromVersionString("42.3"))))
		assertTrue((OpenSuse().handlesVersion(Version.fromVersionString("42.1"))))
	}
}