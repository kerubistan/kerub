package com.github.kerubistan.kerub.planner.steps.vstorage.fs.unallocate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.client.subsystem.sftp.SftpClient
import org.junit.Test
import java.math.BigInteger

class UnAllocateFsExecutorTest {
	@Test
	fun execute() {
		val hostExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()
		val sftpClient = mock<SftpClient>()
		whenever(session.createSftpClient()).thenReturn(sftpClient)
		whenever(hostExecutor.execute(eq(testHost), any<(ClientSession) -> BigInteger>())).then {
			(it.arguments[1] as (ClientSession) -> Unit).invoke(session)
		}
		val vssDynDao = mock<VirtualStorageDeviceDynamicDao>()
		UnAllocateFsExecutor(hostExecutor, vssDynDao).execute(
				UnAllocateFs(
						vstorage = testDisk,
						allocation = VirtualStorageFsAllocation(
								hostId = testHost.id,
								actualSize = 10.GB,
								mountPoint = "/kerub",
								fileName = "test.qcow2",
								type = VirtualDiskFormat.qcow2,
								capabilityId = testFsCapability.id
						),
						host = testHost
				)
		)

		verify(sftpClient).remove(eq("/kerub/test.qcow2"))
	}
}