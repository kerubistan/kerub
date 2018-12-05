package com.github.kerubistan.kerub.planner.steps.vstorage.fs.fallocate

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
import com.github.kerubistan.kerub.utils.now
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertTrue

class FallocateImageExecutorTest {

	private val session: ClientSession = mock()
	private val executor: HostCommandExecutor = mock()
	private val dynDao: VirtualStorageDeviceDynamicDao = mock()

	@Test
	fun execute() {

		whenever(executor.execute(host = eq(testHost), closure = any<(ClientSession) -> Any>())).then {
			(it.arguments[1] as (ClientSession) -> Any).invoke(session)
		}
		session.mockCommandExecution("du.*", output = "1234\t/kerub/test.raw")
		session.mockCommandExecution("fallocate.*")

		var updatedDyn: VirtualStorageDeviceDynamic? = null

		whenever(dynDao.update(eq(testDisk.id), retrieve = any(), change = any())).then {
			updatedDyn = (it.arguments[2] as (VirtualStorageDeviceDynamic) -> VirtualStorageDeviceDynamic)
					.invoke(VirtualStorageDeviceDynamic(id = testDisk.id, lastUpdated = now(), allocations = listOf()))
			updatedDyn
		}

		val allocation = VirtualStorageFsAllocation(
				actualSize = 0.GB,
				fileName = "test.raw",
				hostId = testHost.id,
				mountPoint = "/kerub/",
				type = VirtualDiskFormat.raw,
				capabilityId = testFsCapability.id
		)
		FallocateImageExecutor(executor, dynDao)
				.execute(
						FallocateImage(
								host = testHost,
								allocation = allocation,
								virtualStorage = testDisk,
								expectedFree = 10.GB
						)
				)

		assertTrue {
			updatedDyn?.allocations?.single().let {
				it is VirtualStorageFsAllocation
						&& it.actualSize == 1234.toBigInteger()
			}
		}
	}
}