package com.github.kerubistan.kerub.utils.junix.ssh.openssh

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.toInputStream
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.util.EnumSet
import kotlin.test.assertEquals

class OpenSshTest {

	val client: SshClient = mock()
	val session: ClientSession = mock()
	val sftpClient: SftpClient = mock()
	val handle: SftpClient.CloseableHandle = mock()
	val attrs: SftpClient.Attributes = mock()

	@Test
	fun keyGen() {
		session.mockCommandExecution("ssh-keygen -t rsa -N ''.*+".toRegex())
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(any())).then {
			"TEST PUBLIC KEY".toInputStream()
		}

		val pubKey = OpenSsh.keyGen(session)

		assertEquals("TEST PUBLIC KEY", pubKey)
	}

	@Test
	fun keyGenWithPassword() {
		session.mockCommandExecution("ssh-keygen -t rsa -N.*".toRegex())
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(any())).then {
			"TEST PUBLIC KEY".toInputStream()
		}

		val pubKey = OpenSsh.keyGen(session, "seeecret")

		assertEquals("TEST PUBLIC KEY", pubKey)
	}

	@Test
	fun authorize() {
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.stat(eq(".ssh"))).thenReturn(attrs)
		whenever(sftpClient.open(eq(".ssh/authorized_keys"), any<SftpClient.OpenMode>())).thenReturn(handle)
		whenever(sftpClient.open(eq(".ssh/authorized_keys"), any<EnumSet<SftpClient.OpenMode>>())).thenReturn(handle)
		whenever(sftpClient.stat(eq(".ssh/authorized_keys"))).thenReturn(SftpClient.Attributes())
		whenever(sftpClient.stat(any<SftpClient.CloseableHandle>())).thenReturn(SftpClient.Attributes())
		OpenSsh.authorize(session, pubkey = "TEST")

		verify(sftpClient).close()
		verify(sftpClient, never()).mkdir(any())
	}

	@Test
	fun copyBlockDevice() {
		session.mockCommandExecution(".*".toRegex())
		OpenSsh.copyBlockDevice(
				session,
				sourceDevice = "/dev/mapper/vg-1/vol-1",
				targetDevice = "/dev/mapper/vg-2/vol-2",
				targetAddress = "host-2.example.com")

		session.verifyCommandExecution(".*ssh.*".toRegex())

	}

	@Test
	fun copyBlockDeviceWithCompression() {
		session.mockCommandExecution(".*".toRegex())
		OpenSsh.copyBlockDevice(
				session,
				sourceDevice = "/dev/mapper/vg-1/vol-1",
				targetDevice = "/dev/mapper/vg-2/vol-2",
				targetAddress = "host-2.example.com",
				filters = "gzip -1" to "gzip -dc")

		session.verifyCommandExecution(".*gzip -1.*gzip -dc.*".toRegex())

	}

	@Test
	fun verifySshConnection() {
		session.mockCommandExecution(".*host1.example.com.*".toRegex())
		OpenSsh.verifySshConnection(session, "host1.example.com")
		session.verifyCommandExecution(".*host1.example.com.*".toRegex())
	}

}