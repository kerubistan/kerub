package com.github.kerubistan.kerub.planner.steps.storage.mount

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID

class UnmountNfsExecutorTest {

	private val configurationDao = mock<HostConfigurationDao>()
	private val executor = mock<HostCommandExecutor>()
	private val session = mock<ClientSession>()
	private val channelExec = mock<ChannelExec>()
	private val openFuture = mock<OpenFuture>()

	@Test
	fun execute() {
		val host = testHost.copy(
				id = UUID.randomUUID()
		)
		val nfsServer = testHost.copy(
				id = UUID.randomUUID()
		)

		whenever(session.createExecChannel(any())).thenReturn(channelExec)
		whenever(channelExec.open()).thenReturn(openFuture)
		doAnswer {
			(it.arguments[1] as (ClientSession) -> Unit).invoke(session)
		}.whenever(executor).execute(eq(host), any<(ClientSession) -> Unit>())
		whenever(channelExec.invertedErr).then { NullInputStream(0) }
		whenever(channelExec.invertedOut).then { NullInputStream(0) }

		doAnswer {
			(it.arguments[2] as ((HostConfiguration) -> HostConfiguration)).invoke(HostConfiguration(id = host.id))
		}.whenever(configurationDao).update(eq(host.id), any(), any())

		UnmountNfsExecutor(hostCommandExecutor = executor, hostConfigurationDao = configurationDao).execute(
				UnmountNfs(host = host, mountDir = "/mnt/${nfsServer.id}")
		)

		verify(session).createExecChannel(argThat { startsWith("umount ") })
		verify(configurationDao).update(eq(host.id), any(), any())
	}
}