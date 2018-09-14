package com.github.kerubistan.kerub.planner.steps.vstorage.mount

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MountNfsExecutorTest {

	private val configurationDao = mock<HostConfigurationDao>()
	private val executor = mock<HostCommandExecutor>()
	private val session = mock<ClientSession>()

	@Test
	fun execute() {
		val remote = testHost.copy(id = UUID.randomUUID())
		val local = testHost.copy(id = UUID.randomUUID())

		session.mockCommandExecution("mkdir.*")
		session.mockCommandExecution("mount.*")
		doAnswer {
			(it.arguments[1] as (ClientSession) -> Unit).invoke(session)
		}.whenever(executor).execute(eq(local), any<(ClientSession) -> Unit>())

		var updatedConfig : HostConfiguration? = null

		doAnswer {
			updatedConfig = (it.arguments[2] as ((HostConfiguration) -> HostConfiguration))
					.invoke(HostConfiguration(id = local.id))
		}.whenever(configurationDao).update(eq(local.id), any(), any())

		MountNfsExecutor(executor, configurationDao).execute(
				MountNfs(remoteDirectory = "/kerub", directory = "/mnt", remoteHost = remote, host = local)
		)

		verify(session).createExecChannel(argThat {
			startsWith("mount ")
		})
		verify(configurationDao).update(eq(local.id), any(), any())
		assertNotNull(updatedConfig)
		assertTrue(updatedConfig!!.services.single() is NfsMount)
	}
}