package com.github.kerubistan.kerub.utils.junix.iscsi.tgtd

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.Centos
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TgtAdminTest {

	private val session: ClientSession = mock()

	private val openFuture: OpenFuture = mock()

	private val execChannel: ChannelExec = mock()

	private val sftpClient: SftpClient = mock()

	private val id = UUID.randomUUID()

	@Test
	fun available() {
		assertTrue(
				TgtAdmin.available(
						testHostCapabilities.copy(
								distribution = SoftwarePackage(name = Centos, version = Version.fromVersionString("7.5")),
								installedSoftware = listOf(
										SoftwarePackage(name = "scsi-target-utils", version = Version.fromVersionString("1.2.3"))
								)
						)
				)
		)
		assertFalse(
				TgtAdmin.available(
						testHostCapabilities.copy(
								distribution = SoftwarePackage(name = Centos, version = Version.fromVersionString("7.5")),
								installedSoftware = listOf()
						)
				)
		)
	}

	@Test
	fun shareBlockDevice() {
		val out = ByteArrayOutputStream()
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.write(any())).thenReturn(out)
		session.mockCommandExecution("tgt-admin -e".toRegex())
		TgtAdmin.shareBlockDevice(session, id, "/dev/mapper/bla-bla")

		val config = out.toByteArray().toString(Charsets.US_ASCII)
		assertTrue(config.isNotEmpty())
		assertFalse(config.contains("incominguser"))
		assertFalse(config.contains("readonly 1"))
	}

	@Test
	fun shareBlockDeviceWithPassword() {
		val out = ByteArrayOutputStream()
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.write(any())).thenReturn(out)
		session.mockCommandExecution("tgt-admin -e".toRegex())
		TgtAdmin.shareBlockDevice(
				session = session,
				id = id,
				path = "/dev/mapper/bla-bla",
				password = "TEST-PASSWORD"
		)

		val config = out.toByteArray().toString(Charsets.US_ASCII)
		assertTrue(config.isNotEmpty())
		assertTrue(config.contains("TEST-PASSWORD"))
		assertFalse(config.contains("readonly 1"))
	}

	@Test
	fun shareBlockDeviceWithReadonly() {
		val out = ByteArrayOutputStream()
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(sftpClient.write(any())).thenReturn(out)
		TgtAdmin.shareBlockDevice(
				session = session,
				id = id,
				path = "/dev/mapper/bla-bla",
				readOnly = true
		)

		val config = out.toByteArray().toString(Charsets.US_ASCII)
		assertTrue(config.contains("readonly 1"))
	}

	@Test
	fun unshareBlockDevice() {
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		session.mockCommandExecution("tgt-admin --delete .*".toRegex())

		TgtAdmin.unshareBlockDevice(session, id)

		verify(sftpClient).remove(any())
	}
}