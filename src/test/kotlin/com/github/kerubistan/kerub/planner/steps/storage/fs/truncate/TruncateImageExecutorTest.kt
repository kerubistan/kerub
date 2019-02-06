package com.github.kerubistan.kerub.planner.steps.storage.fs.truncate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class TruncateImageExecutorTest {

	@Test
	fun execute() {
		val session = mock<ClientSession>()
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val dynamicDao = mock<VirtualStorageDeviceDynamicDao>()
		var updatedDynamic : VirtualStorageDeviceDynamic? = null

		whenever(hostCommandExecutor.execute(host = eq(testHost), closure = any<(ClientSession) -> Any>()))
				.then {
					(it.arguments[1] as (ClientSession) -> Any).invoke(session)
				}
		whenever(dynamicDao.update(id = eq(testDisk.id), retrieve = any(), change = any())).then {
			val origDyn = (it.arguments[1] as (UUID) -> VirtualStorageDeviceDynamic).invoke(testDisk.id)
			updatedDynamic = (it.arguments[2] as (VirtualStorageDeviceDynamic) -> VirtualStorageDeviceDynamic).invoke(origDyn)
			updatedDynamic
		}

		session.mockCommandExecution("truncate.*".toRegex())

		val fsAllocation = VirtualStorageFsAllocation(
				hostId = testHost.id,
				actualSize = 10.GB,
				fileName = "${testDisk.id}.raw",
				mountPoint = "/kerub",
				type = VirtualDiskFormat.raw,
				capabilityId = testFsCapability.id
		)
		TruncateImageExecutor(hostCommandExecutor, dynamicDao)
				.execute(TruncateImage(
						host = testHost,
						disk = testDisk,
						allocation = fsAllocation,
						capability = testFsCapability)
				)

		verify(session).createExecChannel(argThat { startsWith("truncate") })
		assertEquals(listOf(fsAllocation), updatedDynamic?.allocations)
	}
}