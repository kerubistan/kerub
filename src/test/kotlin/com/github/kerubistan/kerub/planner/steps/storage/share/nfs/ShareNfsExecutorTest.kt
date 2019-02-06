package com.github.kerubistan.kerub.planner.steps.storage.share.nfs

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import kotlin.test.assertTrue

class ShareNfsExecutorTest {

	@Test
	fun execute() {

		val session = mock<ClientSession>()

		val executor = mock<HostCommandExecutor>()
		val hostConfigDao = mock<HostConfigurationDao>()
		val sftpClient = mock<SftpClient>()
		val handle = mock<SftpClient.CloseableHandle>()

		whenever(executor.execute(eq(testHost), any<(ClientSession) -> Unit>())).then {
			(it.arguments[1] as (ClientSession) -> Unit).invoke(session)
		}

		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(sftpClient.open(any(), any<Collection<SftpClient.OpenMode>>())).thenReturn(handle)
		var bytes : ByteArray? = null
		whenever(sftpClient.write(eq(handle), any(), any(), any(), any())).then {
			bytes = it.arguments[2] as ByteArray
			""
		}
		whenever(sftpClient.stat(eq(handle))).thenReturn(mock())
		session.mockCommandExecution(commandMatcher = "exportfs.*".toRegex())

		ShareNfsExecutor(hostConfigDao, executor).execute(ShareNfs("/kerub", testHost))

		verify(session).createExecChannel(eq("exportfs -r"))
		assertTrue { bytes!!.toString(Charsets.US_ASCII).lines().any { it.startsWith("/kerub") } }
	}
}