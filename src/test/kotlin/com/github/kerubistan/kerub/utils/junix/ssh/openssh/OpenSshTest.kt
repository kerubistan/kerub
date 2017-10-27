package com.github.kerubistan.kerub.utils.junix.ssh.openssh

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import org.mockito.Matchers
import java.util.EnumSet

class OpenSshTest {

	val client: SshClient = mock()
	val execChannel: ChannelExec = mock()
	val openFuture: OpenFuture = mock()
	val session: ClientSession = mock()
	val sftClient: SftpClient = mock()
	val handle: SftpClient.CloseableHandle = mock()
	val attrs: SftpClient.Attributes = mock()

	@Test
	fun keyGen() {
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(openFuture)
		whenever(execChannel.invertedErr).thenReturn(NullInputStream(0))
		whenever(execChannel.invertedOut).thenReturn(NullInputStream(0))

		OpenSsh.keyGen(session)
	}

	@Test
	fun authorize() {
		whenever(session.createSftpClient()).thenReturn(sftClient)
		whenever(sftClient.stat(eq(".ssh"))).thenReturn(attrs)
		whenever(sftClient.open(eq(".ssh/authorized_keys"), Matchers.any<SftpClient.OpenMode>())).thenReturn(handle)
		whenever(sftClient.open(eq(".ssh/authorized_keys"), Matchers.any<EnumSet<SftpClient.OpenMode>>())).thenReturn(handle)
		whenever(sftClient.stat(eq(".ssh/authorized_keys"))).thenReturn(SftpClient.Attributes())
		whenever(sftClient.stat(any<SftpClient.CloseableHandle>())).thenReturn(SftpClient.Attributes())
		OpenSsh.authorize(session, pubkey = "TEST")

		verify(sftClient).close()
		verify(sftClient, never()).mkdir(any())
	}
}