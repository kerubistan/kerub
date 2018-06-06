package com.github.kerubistan.kerub.planner.steps.host.security.install

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.jupiter.api.Test

internal class InstallPublicKeyExecutorTest {

	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val hostCfgDao = mock<HostConfigurationDao>()
		val clientSession = mock<ClientSession>()
		val sftpClient = mock<SftpClient>()
		whenever(clientSession.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.open(any(), any<Collection<SftpClient.OpenMode>>())).thenReturn(mock())
		whenever(sftpClient.stat(any<SftpClient.Handle>())).thenReturn(mock())
		whenever(sftpClient.stat(any<String>())).thenReturn(mock())

		whenever(hostCommandExecutor.execute(eq(testHost), any<(ClientSession) -> Any>())).then {
			(it.arguments[1] as ((ClientSession) -> Any)).invoke(clientSession)
		}

		whenever(hostCommandExecutor.execute(eq(testFreeBsdHost), any<(ClientSession) -> Any>())).then {
			(it.arguments[1] as ((ClientSession) -> Any)).invoke(clientSession)
		}

		whenever(hostCfgDao.updateWithDefault(id = eq(testHost.id), defaultValue = any(), change = any())).then {
			val cfg = (it.arguments[1] as () -> HostConfiguration).invoke()
			(it.arguments[2] as (HostConfiguration) -> HostConfiguration).invoke(cfg)
		}

		InstallPublicKeyExecutor(hostCommandExecutor, hostCfgDao).execute(
				InstallPublicKey(sourceHost = testFreeBsdHost, targetHost = testHost, publicKey = "ssh-rsa blah blah")
		)

		verify(hostCfgDao).updateWithDefault(id = eq(testHost.id), defaultValue = any(), change = any())
	}
}