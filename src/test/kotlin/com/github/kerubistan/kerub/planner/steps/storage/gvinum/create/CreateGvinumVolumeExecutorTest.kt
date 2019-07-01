package com.github.kerubistan.kerub.planner.steps.storage.gvinum.create

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFreeBsdCapabilities
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testGvinumCapability
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.TB
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.io.ByteArrayOutputStream

class CreateGvinumVolumeExecutorTest {
	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val virtualDiskDynDao = mock<VirtualStorageDeviceDynamicDao>()
		val hostDynamicDao = mock<HostDynamicDao>()
		val host = testFreeBsdHost.copy(
				capabilities = testFreeBsdCapabilities.copy(
						storageCapabilities = listOf(
								testGvinumCapability
						)
				)
		)
		val session = mock<ClientSession>()
		val sftpClient = mock<SftpClient>()
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		val testOutputStream = ByteArrayOutputStream()
		whenever(sftpClient.write(any())).thenReturn(testOutputStream)
		hostCommandExecutor.mockHost(host, session)
		session.mockCommandExecution("gvinum create .*".toRegex())
		session.mockCommandExecution(
				"gvinum ld .*".toRegex(), """"1 drives:
			|Drive gvinum-disk-1:	Device ada1
			|Size:       5368573440 bytes (5119 MB)
			|Used:        536870912 bytes (512 MB)
			|Available:  4831702528 bytes (4607 MB)
			|State: up
			|Flags: 0
		""".trimMargin())
		doAnswer { invocation ->
			(invocation.arguments[2] as (HostDynamic) -> HostDynamic).invoke(
					hostUp(host).copy(
							storageStatus = listOf(
									SimpleStorageDeviceDynamic(id = testGvinumCapability.id, freeCapacity = 2.TB))))
		}.whenever(hostDynamicDao).update(eq(host.id), retrieve = any(), change = any<(HostDynamic) -> HostDynamic>())
		CreateGvinumVolumeExecutor(hostCommandExecutor, virtualDiskDynDao, hostDynamicDao).execute(
				CreateGvinumVolume(
						host = host,
						capability = testGvinumCapability,
						disk = testDisk,
						config = SimpleGvinumConfiguration(
								diskName = testGvinumCapability.devices.first().name
						)
				)
		)

		verify(hostDynamicDao).update(eq(host.id), retrieve = any(), change = any<(HostDynamic) -> HostDynamic>())
		session.verifyCommandExecution("gvinum create .*".toRegex())
	}

}