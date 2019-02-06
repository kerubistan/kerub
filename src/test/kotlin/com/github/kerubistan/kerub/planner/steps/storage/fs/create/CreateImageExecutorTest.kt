package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.utils.junix.qemu.ImageInfo
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class CreateImageExecutorTest {

	private val hostCommandExecutor: HostCommandExecutor = mock()
	private val virtualStorageDynamicDao: VirtualStorageDeviceDynamicDao = mock()
	private val session: ClientSession = mock()

	@Test
	fun execute() {

		session.mockCommandExecution("qemu-img create.*".toRegex())

		session.mockCommandExecution("qemu-img info.*".toRegex(),
				output = """
{
    "virtual-size": 104857600,
    "filename": "foo.qcow2",
    "cluster-size": 65536,
    "format": "qcow2",
    "actual-size": 4984832,
    "format-specific": {
        "type": "qcow2",
        "data": {
            "compat": "1.1",
            "lazy-refcounts": false,
            "refcount-bits": 16,
            "corrupt": false
        }
    },
    "dirty-flag": false
}
"""
		)
		val step = CreateImage(
				disk = testDisk,
				host = testHost,
				format = VirtualDiskFormat.qcow2,
				capability = testFsCapability
		)

		whenever(hostCommandExecutor.execute(eq(testHost), any<(ClientSession) -> ImageInfo>()))
				.thenAnswer {
					(it.arguments[1] as (ClientSession) -> ImageInfo).invoke(session)
				}

		whenever(virtualStorageDynamicDao.add(any())).thenAnswer {
			val value = it.arguments[0] as VirtualStorageDeviceDynamic
			assertEquals(value.allocations.single().actualSize, BigInteger.valueOf(4984832))
			assertEquals(value.allocations.single().hostId, step.host.id)
			assert(value.allocations.single() is VirtualStorageFsAllocation)
			assertEquals((value.allocations.single() as VirtualStorageFsAllocation).type, step.format)
			value.id
		}

		CreateImageExecutor(hostCommandExecutor, virtualStorageDynamicDao).execute(step)

		verify(virtualStorageDynamicDao).add(any())
	}
}