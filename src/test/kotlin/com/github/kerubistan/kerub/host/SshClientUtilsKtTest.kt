package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.expect
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.Charset

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

		expect(IOException::class,  {session.executeOrDie("echo hello")})

	}

}