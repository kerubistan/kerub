package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.getTestKey
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.apache.sshd.common.SshException
import org.apache.sshd.common.kex.KeyExchange
import org.apache.sshd.common.session.SessionListener
import org.apache.sshd.common.session.helpers.AbstractSession
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.security.PublicKey
import java.util.EnumSet
import java.util.concurrent.TimeUnit


class SshClientServiceImplTest {
	val client : SshClient = mock()
	val session : ClientSession = mock()
	val sftClient : SftpClient = mock()
	val serverPublicKey: PublicKey = mock()
	val handle: SftpClient.CloseableHandle = mock()

	var service : SshClientServiceImpl? = null

	@Before
	fun setup() {
		service = SshClientServiceImpl(client = client,
		                               keyPair = getTestKey(),
		                               maxWait = 1000,
		                               maxWaitUnit = TimeUnit.MILLISECONDS
		                              )
	}

	@Test
	fun installPublicKey() {
		whenever(session.createSftpClient()).thenReturn(sftClient)
		whenever(sftClient.stat(eq(".ssh"))).thenReturn(SftpClient.Attributes())
		whenever(sftClient.open(eq(".ssh/authorized_keys"), any<SftpClient.OpenMode>())).thenReturn(handle)
		whenever(sftClient.open(eq(".ssh/authorized_keys"), any<EnumSet<SftpClient.OpenMode>>())).thenReturn(handle)
		whenever(sftClient.stat(eq(".ssh/authorized_keys"))).thenReturn(SftpClient.Attributes())
		whenever(sftClient.stat(eq(handle))).thenReturn(SftpClient.Attributes())
		service!!.installPublicKey(session)

		verify(sftClient).close(eq(handle))
	}

	@Test
	fun getPublicKey() {
		Assert.assertThat(service!!.getPublicKey(), CoreMatchers.notNullValue())
	}

	@Test
	fun sessionEvent() {
		val abstractSession = Mockito.mock(AbstractSession::class.java)!!
		val kex = Mockito.mock(KeyExchange::class.java)
		whenever(abstractSession.kex).thenReturn(kex)
		whenever(kex.serverKey).thenReturn(getTestKey().public)
		SshClientServiceImpl.ServerFingerprintChecker("f6:aa:fa:c7:1d:98:cd:8b:0c:5b:c6:63:bb:3a:73:f6")
				.sessionEvent(abstractSession, SessionListener.Event.KeyEstablished)
	}

	@Test
	fun sessionEventWithWrongKey() {
		val abstractSession = Mockito.mock(AbstractSession::class.java)!!
		val kex = Mockito.mock(KeyExchange::class.java)
		whenever(abstractSession.kex).thenReturn(kex)
		whenever(kex.serverKey).thenReturn(getTestKey().public)
		expect(SshException::class, {
			SshClientServiceImpl.ServerFingerprintChecker("WRONG")
					.sessionEvent(abstractSession, SessionListener.Event.KeyEstablished)
		})
	}

}