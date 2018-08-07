package com.github.kerubistan.kerub.utils.junix.truncate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertTrue

class TruncateTest {

	private val session = mock<ClientSession>()

	@Test
	fun available() {
		assertTrue("truncate should be part of base installation, available everywhere") {
			Truncate.available(null)
		}
	}

	@Test
	fun truncate() {
		session.mockCommandExecution("truncate -s \\d+ .*".toRegex())
		Truncate.truncate(session = session, path = "/kerub/test.raw", virtualSize = 100.GB)
		verify(session).createExecChannel(argThat { startsWith("truncate") })
	}
}