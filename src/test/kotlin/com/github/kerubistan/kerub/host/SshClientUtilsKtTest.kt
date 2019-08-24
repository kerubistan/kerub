package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.SocketAddress
import java.nio.charset.Charset
import kotlin.test.assertEquals

class SshClientUtilsKtTest {

	val session : ClientSession = mock()
	val execChannel : ChannelExec = mock()
	val openFuture : OpenFuture = mock()

	@Test
	fun testExecuteOrDie() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream("hello".toByteArray(Charset.forName("ASCII"))))

		val result = session.executeOrDie("echo hello")

		Assert.assertEquals("hello", result)
	}

	@Test
	fun testExecuteOrDieAndDie() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(ByteArrayInputStream("error".toByteArray(Charset.forName("ASCII"))))
		whenever(execChannel.invertedOut).thenReturn(ByteArrayInputStream("hello".toByteArray(Charset.forName("ASCII"))))

		assertThrows<IOException> {session.executeOrDie("echo hello")}

	}

	@Test
	fun process() {
		val socketAddress = mock<SocketAddress>()
		whenever(socketAddress.toString()).thenReturn("test")
		whenever(session.connectAddress).thenReturn(socketAddress)
		session.mockProcess(".*".toRegex(), output = "", stderr = "this error will show up in the log\n")
		session.process("TEST", mock {  })
	}

	@Test
	fun bashMonitor() {
		val socketAddress = mock<SocketAddress>()
		whenever(socketAddress.toString()).thenReturn("test")
		whenever(session.connectAddress).thenReturn(socketAddress)
		session.mockProcess("bash .*".toRegex(), output = "TEST-result", stderr = "this error will show up in the log\n")
		val output = ByteArrayOutputStream()
		session.bashMonitor(
				command = "TEST-command",
				separator = "--separator--",
				interval = 10,
				output = output
		)

		assertEquals("TEST-result", output.toByteArray().toString(Charsets.US_ASCII))
	}

}