package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Centos7Test {

	@Test
	fun testGetPackageManager() {
		assertNotNull(Centos7().getPackageManager(Mockito.mock(ClientSession::class.java)))
	}

	@Test
	fun testHandlesVersion() {
		assertTrue(Centos7().handlesVersion(Version.fromVersionString("7")))
		assertTrue(Centos7().handlesVersion(Version.fromVersionString("7.1")))
		assertFalse(Centos7().handlesVersion(Version.fromVersionString("7beta")))
	}
}