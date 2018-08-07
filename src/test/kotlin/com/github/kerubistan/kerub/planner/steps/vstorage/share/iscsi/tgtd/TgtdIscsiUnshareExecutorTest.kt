package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class TgtdIscsiUnshareExecutorTest {

	@Test
	fun execute() {
		val hostConfigDao = mock<HostConfigurationDao>()
		val hostExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()
		val channelExec = mock<ChannelExec>()
		val openFuture = mock<OpenFuture>()
		val sftpClient = mock<SftpClient>()
		val allocation = VirtualStorageLvmAllocation(
				hostId = testHost.id,
				vgName = "test-vg",
				actualSize = 10.GB,
				path = "/dev/test-vg/test-lv"
		)
		val otherVstorageId = UUID.randomUUID()

		val hostConfiguration = HostConfiguration(
				id = testHost.id,
				services = listOf(
						IscsiService(vstorageId = testDisk.id),
						IscsiService(vstorageId = otherVstorageId),
						NfsMount(
								remoteHostId = testFreeBsdHost.id,
								localDirectory = "/kerub/test",
								remoteDirectory = "/kerub"
						)
				)
		)

		var updatedConfiguration : HostConfiguration? = null

		doAnswer {
			updatedConfiguration = (it.arguments[2] as ((HostConfiguration) -> HostConfiguration)).invoke(hostConfiguration)
		}.whenever(hostConfigDao).update(any(), any(), any())

		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(session.createExecChannel(any())).thenReturn(channelExec)
		whenever(channelExec.open()).thenReturn(openFuture)
		whenever(channelExec.invertedErr).then { NullInputStream(0) }
		whenever(channelExec.invertedOut).then { NullInputStream(0) }
		doAnswer {
			(it.arguments[1] as (ClientSession) -> Any).invoke(session)
		}.whenever(hostExecutor).execute(any(), any<(ClientSession) -> Any>())

		TgtdIscsiUnshareExecutor(hostConfigDao, hostExecutor)
				.execute(TgtdIscsiUnshare(host = testHost, vstorage = testDisk, allocation = allocation))


		assertEquals(
				updatedConfiguration,
				HostConfiguration(
						id = testHost.id,
						services = listOf(
								IscsiService(vstorageId = otherVstorageId),
								NfsMount(
										remoteHostId = testFreeBsdHost.id,
										localDirectory = "/kerub/test",
										remoteDirectory = "/kerub"
								)
						)
				)
		)
		verify(hostConfigDao).update(any(), any(), any())
	}
}

