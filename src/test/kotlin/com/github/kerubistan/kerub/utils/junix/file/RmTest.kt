package com.github.kerubistan.kerub.utils.junix.file

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertTrue
import org.junit.Test

class RmTest {

	@Test
	fun available() {
		assertTrue("rm should be available on any unix-like OS", Rm.available(null))
	}

	@Test
	fun remove() {
		val session = mock<ClientSession>()
		session.mockCommandExecution("rm .*".toRegex())
		Rm.remove(session, "/tmp/deleteme")
		verify(session).createExecChannel(argThat { startsWith("rm") })
	}
}