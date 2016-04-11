package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version
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
		assertTrue((OpenSuse().handlesVersion(Version.fromVersionString("13"))))
		assertTrue((OpenSuse().handlesVersion(Version.fromVersionString("13.1"))))
		assertTrue((OpenSuse().handlesVersion(Version.fromVersionString("13.2"))))
		assertTrue((OpenSuse().handlesVersion(Version.fromVersionString("13.3"))))
		assertFalse((OpenSuse().handlesVersion(Version.fromVersionString("14"))))
		assertFalse((OpenSuse().handlesVersion(Version.fromVersionString("12"))))
	}
}