package com.github.K0zka.kerub.utils.junix.virt.virsh

import com.github.K0zka.kerub.anyString
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.ClientSession
import org.apache.sshd.client.SftpClient
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class VirshTest {

	@Mock
	var session: ClientSession? = null

	@Mock
	var execChannel : ChannelExec? = null

	@Mock
	var channelOpenFuture : OpenFuture? = null

	@Mock
	var sftpClient : SftpClient? = null

	@Before
	fun setup() {
		Mockito.`when`(session!!.createExecChannel(Matchers.anyString() ?: "")).thenReturn(execChannel!!)
		Mockito.`when`(execChannel!!.open()).thenReturn(channelOpenFuture)
		Mockito.`when`(session!!.createSftpClient()).thenReturn(sftpClient)
	}

	@Test
	fun create() {
		Mockito.`when`(sftpClient!!.write(anyString())).thenReturn(ByteArrayOutputStream())
		Mockito.`when`(execChannel!!.invertedErr)
				.thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedOut)
				.thenReturn(NullInputStream(0))
		Virsh.create(session!!, UUID.randomUUID(), "TEST-DOMAIN-DEF")
	}

	@Test
	fun createAndFail() {
		try {
			Mockito.`when`(sftpClient!!.write(anyString())).thenReturn(ByteArrayOutputStream())
			Mockito.`when`(execChannel!!.invertedErr)
					.thenReturn(ByteArrayInputStream("TEST ERROR".toByteArray("ASCII")))
			Mockito.`when`(execChannel!!.invertedOut)
					.thenReturn(NullInputStream(0))
			Virsh.create(session!!, UUID.randomUUID(), "TEST-DOMAIN-DEF")
		} catch(e : IOException) {
			Mockito.verify(sftpClient)!!.write(anyString())
			Mockito.verify(sftpClient)!!.remove(anyString())
		}
	}

	@Test
	fun list() {
		val testOutput =
				"""8952908a-f27d-45dc-b274-0aeb7a68660a
8952908a-f27d-45dc-b274-0aeb7a68660b
8952908a-f27d-45dc-b274-0aeb7a68660c"""
		Mockito.`when`(execChannel!!.invertedOut)
				.thenReturn(ByteArrayInputStream(testOutput.toByteArray("ASCII")))
		Mockito.`when`(execChannel!!.invertedErr)
				.thenReturn(NullInputStream(0))

		val ids = Virsh.list(session!!)

		Assert.assertEquals(ids, listOf(UUID.fromString("8952908a-f27d-45dc-b274-0aeb7a68660a"),
				UUID.fromString("8952908a-f27d-45dc-b274-0aeb7a68660b"),
				UUID.fromString("8952908a-f27d-45dc-b274-0aeb7a68660c")))
	}

	@Test
	fun suspend() {
		Mockito.`when`(execChannel!!.invertedOut)
				.thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedErr)
				.thenReturn(NullInputStream(0))
		Virsh.suspend(session!!, UUID.randomUUID())

		Mockito.verify(session)!!.createExecChannel(Matchers.startsWith("virsh suspend") ?: "")
	}

	@Test
	fun resume() {
		Mockito.`when`(execChannel!!.invertedOut)
				.thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedErr)
				.thenReturn(NullInputStream(0))
		Virsh.resume(session!!, UUID.randomUUID())

		Mockito.verify(session)!!.createExecChannel(Matchers.startsWith("virsh resume") ?: "")
	}

}