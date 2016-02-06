package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.getTestKey
import org.apache.sshd.ClientSession
import org.apache.sshd.SshClient
import org.apache.sshd.client.SftpClient
import org.apache.sshd.common.KeyExchange
import org.apache.sshd.common.SessionListener
import org.apache.sshd.common.SshException
import org.apache.sshd.common.session.AbstractSession
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.security.PublicKey
import java.util.concurrent.TimeUnit


@RunWith(MockitoJUnitRunner::class) class SshClientServiceImplTest {
	@Mock
	var client : SshClient? = null
	@Mock
	var session : ClientSession? = null
	@Mock
	var sftClient : SftpClient? = null

	@Mock
	var serverPublicKey: PublicKey? = null

	var service : SshClientServiceImpl? = null

	@Before
	fun setup() {
		service = SshClientServiceImpl(client = client!!,
		                               keyPair = getTestKey(),
		                               maxWait = 1000,
		                               maxWaitUnit = TimeUnit.MILLISECONDS
		                              )
	}

	@Test
	fun installPublicKey() {
		Mockito.`when`(session!!.createSftpClient()).thenReturn(sftClient)
		Mockito.`when`(sftClient!!.stat(eq(".ssh"))).thenReturn(SftpClient.Attributes())
		val authorizedKeysHandle = SftpClient.Handle("TEST")
		Mockito.`when`(sftClient!!.open(eq(".ssh/authorized_keys"), Matchers.any())).thenReturn(authorizedKeysHandle)
		Mockito.`when`(sftClient!!.stat(eq(".ssh/authorized_keys"))) .thenReturn(SftpClient.Attributes())
		Mockito.`when`(sftClient!!.stat(eq(authorizedKeysHandle))).thenReturn(SftpClient.Attributes())
		service!!.installPublicKey(session!!)
	}

	@Test
	fun getPublicKey() {
		Assert.assertThat(service!!.getPublicKey(), CoreMatchers.notNullValue())
	}

	@Test
	fun sessionEvent() {
		val abstractSession = Mockito.mock(AbstractSession::class.java)!!
		val kex = Mockito.mock(KeyExchange::class.java)
		Mockito.`when`(abstractSession.kex).thenReturn(kex)
		Mockito.`when`(kex.serverKey).thenReturn(getTestKey().public)
		SshClientServiceImpl.ServerFingerprintChecker("f6:aa:fa:c7:1d:98:cd:8b:0c:5b:c6:63:bb:3a:73:f6")
				.sessionEvent(abstractSession, SessionListener.Event.KeyEstablished)
	}

	@Test
	fun sessionEventWithWrongKey() {
		val abstractSession = Mockito.mock(AbstractSession::class.java)!!
		val kex = Mockito.mock(KeyExchange::class.java)
		Mockito.`when`(abstractSession.kex).thenReturn(kex)
		Mockito.`when`(kex.serverKey).thenReturn(getTestKey().public)
		expect(SshException::class, {
			SshClientServiceImpl.ServerFingerprintChecker("WRONG")
					.sessionEvent(abstractSession, SessionListener.Event.KeyEstablished)
		})
	}

}