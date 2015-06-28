package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.getTestKey
import com.github.K0zka.kerub.matchAny
import org.apache.sshd.ClientSession
import org.apache.sshd.SshClient
import org.apache.sshd.client.SftpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.*
import java.util.concurrent.TimeUnit


RunWith(MockitoJUnitRunner::class)
public class SshClientServiceImplTest {
	Mock
	var client : SshClient? = null
	Mock
	var session : ClientSession? = null
	Mock
	var sftClient : SftpClient? = null

	var service : SshClientServiceImpl? = null

	Before
	fun setup() {
		service = SshClientServiceImpl(client = client!!,
		                               keyPair = getTestKey(),
		                               maxWait = 1000,
		                               maxWaitUnit = TimeUnit.MILLISECONDS
		                              )
	}

	Test
	fun installPublicKey() {
		Mockito.`when`(session!!.createSftpClient()).thenReturn(sftClient)
		Mockito.`when`(sftClient!!.stat(eq(".ssh"))).thenReturn(SftpClient.Attributes())
		val authorizedKeysHandle = SftpClient.Handle("TEST")
		Mockito.`when`(sftClient!!.open(eq(".ssh/authorized_keys"), Matchers.any())).thenReturn(authorizedKeysHandle)
		Mockito.`when`(sftClient!!.stat(eq(".ssh/authorized_keys"))) .thenReturn(SftpClient.Attributes())
		Mockito.`when`(sftClient!!.stat(eq(authorizedKeysHandle))).thenReturn(SftpClient.Attributes())
		service!!.installPublicKey(session!!)


	}
}