package com.github.kerubistan.kerub.planner.steps.vstorage.fs.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.utils.junix.qemu.ImageInfo
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.io.ByteArrayInputStream
import java.math.BigInteger
import kotlin.test.assertEquals

class CreateImageExecutorTest {

	val hostCommandExecutor: HostCommandExecutor = mock()
	val virtualStorageDynamicDao: VirtualStorageDeviceDynamicDao = mock()
	val session: ClientSession = mock()
	val createExec: ChannelExec = mock()
	val infoExec: ChannelExec = mock()
	val open: OpenFuture = mock()

	@Test
	fun execute() {

		whenever(session.createExecChannel(argThat { startsWith("qemu-img create") })).thenReturn(createExec)
		whenever(createExec.open()).thenReturn(open)
		whenever(createExec.invertedErr).then { NullInputStream(0) }
		whenever(createExec.invertedOut).then { NullInputStream(0) }

		whenever(session.createExecChannel(argThat { startsWith("qemu-img info") })).thenReturn(infoExec)
		whenever(infoExec.open()).thenReturn(open)
		whenever(infoExec.invertedErr).then { NullInputStream(0) }
		whenever(infoExec.invertedOut).then {
			ByteArrayInputStream("""
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
""".toByteArray())
		}
		val step = CreateImage(
				disk = testDisk,
				host = testHost,
				path = "/kerub/${testDisk.id}",
				format = VirtualDiskFormat.qcow2
		)

		whenever(hostCommandExecutor.execute(eq(testHost), any<(ClientSession) -> ImageInfo>()))
				.thenAnswer {
					(it.arguments[1] as (ClientSession) -> ImageInfo).invoke(session)
				}

		whenever(virtualStorageDynamicDao.add(any())).thenAnswer {
			val value = it.arguments[0] as VirtualStorageDeviceDynamic
			assertEquals(value.allocation.actualSize, BigInteger.valueOf(4984832))
			assertEquals(value.allocation.hostId, step.host.id)
			assert(value.allocation is VirtualStorageFsAllocation)
			assertEquals((value.allocation as VirtualStorageFsAllocation).type, step.format)
			value.id
		}

		CreateImageExecutor(hostCommandExecutor, virtualStorageDynamicDao).execute(
				step
		)

		verify(virtualStorageDynamicDao).add(any())
	}
}