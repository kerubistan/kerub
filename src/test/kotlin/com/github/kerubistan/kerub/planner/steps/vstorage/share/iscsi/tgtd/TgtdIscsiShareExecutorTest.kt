package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.FireWall
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.host.ServiceManager
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.utils.junix.iscsi.tgtd.TgtAdmin
import com.github.kerubistan.kerub.utils.toSize
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.util.UUID

class TgtdIscsiShareExecutorTest {

	val hostConfigDao: HostConfigurationDao = mock()
	val hostManager: HostManager = mock()
	val firewall: FireWall = mock()
	val serviceManager: ServiceManager = mock()
	val session: ClientSession = mock()
	val channel: ChannelExec = mock()
	val sftp: SftpClient = mock()
	val openFuture: OpenFuture = mock()

	val host = Host(
			id = UUID.randomUUID(),
			address = "test-1.example.com",
			publicKey = "",
			dedicated = true
	)

	val vStorage = VirtualStorageDevice(
			id = UUID.randomUUID(),
			name = "disk-1",
			expectations = listOf(),
			size = "16 GB".toSize()
	)

	@Test
	fun testExecute() {
		whenever(hostManager.getFireWall(any())).thenReturn(firewall)
		whenever(session.createExecChannel(any())).thenReturn(channel)
		whenever(channel.open()).thenReturn(openFuture)
		whenever(channel.invertedErr).thenReturn(NullInputStream(0))
		whenever(channel.invertedOut).thenReturn(NullInputStream(0))
		whenever(channel.out).thenReturn(NullOutputStream())
		whenever(session.createSftpClient()).thenReturn(sftp)
		whenever(sftp.write(any())).thenReturn(NullOutputStream())
		whenever(hostManager.getServiceManager(any())).thenReturn(serviceManager)
		TgtdIscsiShareExecutor(hostConfigDao, HostCommandExecutorStub(session), hostManager)
				.execute(
						step = TgtdIscsiShare(
								host = host,
								allocation = VirtualStorageLvmAllocation(
										hostId = host.id,
										path = "",
										actualSize = vStorage.size,
										vgName = "test-vg"),
								vstorage = vStorage
						)
				)

		verify(firewall).open(eq(3260), eq("tcp"))
		verify(serviceManager).start(TgtAdmin)
	}
}