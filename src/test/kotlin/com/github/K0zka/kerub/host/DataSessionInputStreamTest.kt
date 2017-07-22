package com.github.K0zka.kerub.host

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.io.InputStream

class DataSessionInputStreamTest {
	val session: ClientSession = mock()
	val sftp: SftpClient = mock()
	val input: InputStream = mock()

	@Test
	fun close() {
		val stream = HostManagerImpl.Companion.DataSessionInputStream(
				stream = input,
				sftp = sftp,
				session = session
		)
		stream.close()
		verify(sftp).close()
		verify(session).close()
		verify(input).close()
	}

}