package com.github.kerubistan.kerub.utils.junix.iscsi.tgtd

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.io.ByteArrayOutputStream
import java.util.UUID

class TgtAdminTest {

	val session: ClientSession = mock()

	val openFuture: OpenFuture = mock()

	val execChannel: ChannelExec = mock()

	val sftpClient: SftpClient = mock()

	val id: UUID = UUID.randomUUID()

	@Test
	fun shareBlockDevice() {
		val out = ByteArrayOutputStream()
		Mockito.`when`(session.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(execChannel.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(session.createExecChannel(any())).thenReturn(execChannel)
		Mockito.`when`(sftpClient.write(any())).thenReturn(out)
		TgtAdmin.shareBlockDevice(session, id, "/dev/mapper/bla-bla")

		val config = out.toByteArray().toString(Charsets.US_ASCII)
		Assert.assertTrue(config.isNotEmpty())
		Assert.assertFalse(config.contains("incominguser"))
		Assert.assertFalse(config.contains("readonly 1"))
	}

	@Test
	fun shareBlockDeviceWithPassword() {
		val out = ByteArrayOutputStream()
		Mockito.`when`(session.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(execChannel.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(session.createExecChannel(any())).thenReturn(execChannel)
		Mockito.`when`(sftpClient.write(any())).thenReturn(out)
		TgtAdmin.shareBlockDevice(
				session = session,
				id = id,
				path = "/dev/mapper/bla-bla",
				password = "TEST-PASSWORD"
		)

		val config = out.toByteArray().toString(Charsets.US_ASCII)
		Assert.assertTrue(config.isNotEmpty())
		Assert.assertTrue(config.contains("TEST-PASSWORD"))
		Assert.assertFalse(config.contains("readonly 1"))
	}

	@Test
	fun shareBlockDeviceWithReadonly() {
		val out = ByteArrayOutputStream()
		Mockito.`when`(session.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(execChannel.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(session.createExecChannel(any())).thenReturn(execChannel)
		Mockito.`when`(sftpClient.write(any())).thenReturn(out)
		TgtAdmin.shareBlockDevice(
				session = session,
				id = id,
				path = "/dev/mapper/bla-bla",
				readOnly = true
		)

		val config = out.toByteArray().toString(Charsets.US_ASCII)
		Assert.assertTrue(config.contains("readonly 1"))
	}

	@Test
	fun unshareBlockDevice() {
		Mockito.`when`(session.createSftpClient()).thenReturn(sftpClient)
		Mockito.`when`(execChannel.open()).thenReturn(openFuture)
		Mockito.`when`(execChannel.invertedErr).thenReturn(NullInputStream(0))
		Mockito.`when`(execChannel.invertedOut).thenReturn(NullInputStream(0))
		Mockito.`when`(session.createExecChannel(any())).thenReturn(execChannel)

		TgtAdmin.unshareBlockDevice(session, id)

		Mockito.verify(sftpClient)!!.remove(any())
	}
}