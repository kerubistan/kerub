package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FreeBSDTest {

	private val session: ClientSession = mock()

	@Test
	fun handlesVersion() {
		assertTrue(FreeBSD().handlesVersion(Version.fromVersionString("10.0")))
		assertTrue(FreeBSD().handlesVersion(Version.fromVersionString("11.0.RC3")))
	}

	@Test
	fun detect() {
		session.mockCommandExecution("uname -s", output = "FreeBSD")
		assertTrue(FreeBSD().detect(session))
	}

	@Test
	fun detectHostCpuType() {
		session.mockCommandExecution("uname -p", output = "amd64")
		assertEquals("X86_64", FreeBSD().detectHostCpuType(session))
	}

	@Test
	fun getTotalMemory() {
		session.mockCommandExecution("sysctl hw.physmem", output = "hw.physmem: 1040261120")
		val totalMem = FreeBSD().getTotalMemory(session)
		assertEquals(1040261120.toBigInteger(), totalMem)
	}


}