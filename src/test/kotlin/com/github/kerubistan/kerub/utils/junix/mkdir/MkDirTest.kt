package com.github.kerubistan.kerub.utils.junix.mkdir

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertTrue

class MkDirTest {

	@Test
	fun available() {
		assertTrue("mkdir should be available on all unix-like operating systems") {
			MkDir.available(null)
		}
	}

	@Test
	fun mkdir() {
		val session = mock<ClientSession>()
		session.mockCommandExecution("mkdir -p .*")
		MkDir.mkdir(session, "/tmp/test")

		verify(session).createExecChannel(argThat { startsWith("mkdir -p") })
	}
}