package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.anyString
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.on
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.Charset

@RunWith(MockitoJUnitRunner::class)
class SshClientUtilsKtTest {

	@Mock
	var session : ClientSession? = null
	@Mock
	var execChannel : ChannelExec? = null
	@Mock
	var openFuture : OpenFuture? = null

	@Test
	fun testExecuteOrDie() {
		on(session!!.createExecChannel(anyString())).thenReturn(execChannel)
		on(execChannel!!.open()).thenReturn(openFuture)
		on(execChannel!!.invertedErr).thenReturn(NullInputStream(0))
		on(execChannel!!.invertedOut).thenReturn(ByteArrayInputStream("hello".toByteArray(Charset.forName("ASCII"))))

		val result = session!!.executeOrDie("echo hello")

		Assert.assertEquals("hello", result)
	}

	@Test
	fun testExecuteOrDieAndDie() {
		on(session!!.createExecChannel(anyString())).thenReturn(execChannel)
		on(execChannel!!.open()).thenReturn(openFuture)
		on(execChannel!!.invertedErr).thenReturn(ByteArrayInputStream("error".toByteArray(Charset.forName("ASCII"))))
		on(execChannel!!.invertedOut).thenReturn(ByteArrayInputStream("hello".toByteArray(Charset.forName("ASCII"))))

		expect(IOException::class,  {session!!.executeOrDie("echo hello")})

	}

}