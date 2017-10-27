package com.github.kerubistan.kerub.host

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.InputStream

class ControlSessionInputStreamTest {

	val sftp: SftpClient = mock()
	val input: InputStream = mock()

	@Test
	fun read() {
		whenever(input.read()).thenReturn(1)
		val stream = HostManagerImpl.Companion.ControlSessionInputStream(input, sftp)
		assertEquals(1, stream.read())
		verify(input).read()
	}

	@Test
	fun close() {
		val stream = HostManagerImpl.Companion.ControlSessionInputStream(input, sftp)
		stream.close()
		verify(input).close()
		verify(sftp).close()
	}

}