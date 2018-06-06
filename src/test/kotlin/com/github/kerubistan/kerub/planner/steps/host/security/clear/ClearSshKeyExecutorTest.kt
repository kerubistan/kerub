package com.github.kerubistan.kerub.planner.steps.host.security.clear

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class ClearSshKeyExecutorTest {

	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val hostCfgDao = mock<HostConfigurationDao>()
		val session = mock<ClientSession>()
		val sftpClient = mock<SftpClient>()
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(hostCommandExecutor.execute(eq(testHost), any<(ClientSession) -> Any>())).then {
			(it.arguments[1] as (ClientSession) -> Any).invoke(session)
		}
		whenever(hostCfgDao.update(any(), any(), any())).then {
			val updated = (it.arguments[2] as (HostConfiguration) -> HostConfiguration).invoke(
					HostConfiguration(id = testHost.id)
			)
			assertTrue {
				updated.id == testHost.id
			}
			assertTrue {
				updated.publicKey == null
			}
			updated
		}

		ClearSshKeyExecutor(hostCommandExecutor, hostCfgDao).execute(ClearSshKey(host = testHost))

		verify(hostCfgDao).update(eq(testHost.id), retrieve = any(), change =  any())
	}
}