package com.github.kerubistan.kerub.utils.junix.truncate

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import org.junit.jupiter.api.assertThrows
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

	@Test
	fun truncateWithError() {
		session.mockCommandExecution("truncate -s \\d+ .*".toRegex(),
				error = "truncate: failed to truncate 'test3' at 109951162777600 bytes: File too large")
		assertThrows<Exception> {
			Truncate.truncate(session = session, path = "/kerub/test.raw", virtualSize = 100.TB)
		}
		verify(session).createExecChannel(argThat { startsWith("truncate") })
	}

}