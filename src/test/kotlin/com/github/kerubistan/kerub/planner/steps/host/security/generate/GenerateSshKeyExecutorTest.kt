package com.github.kerubistan.kerub.planner.steps.host.security.generate

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.toInputStream
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import kotlin.test.assertTrue

class GenerateSshKeyExecutorTest {

	@Test
	fun execute() {
		val hostCfgDao = mock<HostConfigurationDao>()
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()
		val sftpClient = mock<SftpClient>()
		val execChannel = mock<ChannelExec>()
		whenever(execChannel.open()).thenReturn(mock())
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { NullInputStream(0) }
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.read(any())).thenReturn( "ssh-rsa TEST".toInputStream() )

		whenever(hostCommandExecutor.execute(eq(testHost), any<(ClientSession) -> String>())).then {
			(it.arguments[1] as (ClientSession) -> String).invoke(session)
		}

		whenever(hostCfgDao.updateWithDefault(any(), any(), any())).then {
			val updated = (it.arguments[2] as (HostConfiguration) -> HostConfiguration).invoke(
					HostConfiguration(id = testHost.id)
			)
			assertTrue {
				updated.id == testHost.id
			}
			assertTrue {
				updated.publicKey == "ssh-rsa TEST"
			}
			updated
		}

		GenerateSshKeyExecutor(hostCommandExecutor, hostCfgDao).execute(GenerateSshKey(host = testHost))

		verify(hostCfgDao).updateWithDefault(any(), any(), any())
	}
}