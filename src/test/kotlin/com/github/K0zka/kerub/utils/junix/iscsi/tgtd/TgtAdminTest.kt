package com.github.K0zka.kerub.utils.junix.iscsi.tgtd

import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.io.ByteArrayOutputStream
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class TgtAdminTest {

	@Mock
	var session: ClientSession? = null

	@Mock
	var openFuture: OpenFuture? = null

	@Mock
	var execChannel: ChannelExec? = null

	@Mock
	var sftpClient: SftpClient? = null

	val id = UUID.randomUUID()

	@Test
	fun shareBlockDevice() {
		val out = ByteArrayOutputStream()
		Mockito.`when`(session!!.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(execChannel!!.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel!!.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(session!!.createExecChannel(Matchers.anyString() ?: "")).thenReturn(execChannel)
		Mockito.`when`(sftpClient!!.write(Matchers.anyString() ?: "")).thenReturn(out)
		TgtAdmin.shareBlockDevice(session!!, id, "/dev/mapper/bla-bla")

		Assert.assertTrue(out.toByteArray().size > 0)
	}

	@Test
	fun unshareBlockDevice() {
		Mockito.`when`(session!!.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(execChannel!!.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel!!.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel!!.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(session!!.createExecChannel(Matchers.anyString() ?: "")).thenReturn(execChannel)

		TgtAdmin.unshareBlockDevice(session!!, id)

		Mockito.verify(sftpClient)!!.remove(Matchers.anyString() ?: "")
	}
}