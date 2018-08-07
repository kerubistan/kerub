package com.github.kerubistan.kerub.planner.steps.host.security.remove

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.toInputStream
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import kotlin.test.assertTrue

class RemovePublicKeyExecutorTest {

	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val hostCfgDao = mock<HostConfigurationDao>()
		val clientSession = mock<ClientSession>()
		val sftpClient = mock<SftpClient>()
		whenever(clientSession.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(any())).then { "ssh-rsa AAAAA....".toInputStream() }
		whenever(sftpClient.write(any())).thenReturn(NullOutputStream())

		whenever(hostCommandExecutor.execute(any(), any<(ClientSession) -> Any>())).then {
			(it.arguments[1] as (ClientSession) -> Any).invoke(clientSession)
		}

		whenever(hostCfgDao.update(eq(testHost.id), retrieve = any(), change = any())).then {
			val updatedConfig = (it.arguments[2] as (HostConfiguration) -> HostConfiguration)
					.invoke(HostConfiguration(id = testHost.id, acceptedPublicKeys = listOf("ssh-rsa AAAAA....")))
			assertTrue { updatedConfig.id == testHost.id }
			assertTrue { updatedConfig.acceptedPublicKeys == listOf<String>() }
			updatedConfig
		}

		RemovePublicKeyExecutor(hostCommandExecutor, hostCfgDao).execute(
				RemovePublicKey(
						host = testHost,
						publicKey = "ssh-rsa AAAAA....",
						hostOfKey = testFreeBsdHost
				)
		)

		verify(hostCfgDao).update(eq(testHost.id), retrieve = any(), change = any())
	}
}