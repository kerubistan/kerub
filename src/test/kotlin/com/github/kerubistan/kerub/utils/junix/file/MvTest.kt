package com.github.kerubistan.kerub.utils.junix.file

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertTrue
import org.junit.Test

class MvTest {

	@Test
	fun available() {
		assertTrue("mv should be available on any unix-like OS", Mv.available(null))
	}

	@Test
	fun move() {
		val session = mock<ClientSession>()
		session.mockCommandExecution("mv .*".toRegex())
		Mv.move(session, "/tmp/source", "/tmp/target")
		verify(session).createExecChannel(argThat { startsWith("mv ") })
	}
}