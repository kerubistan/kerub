package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.unallocate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID

internal class UnAllocateLvExecutorTest {
	@Test
	fun execute() {
		val hostExecutor = mock<HostCommandExecutor>()
		val vssDynDao = mock<VirtualStorageDeviceDynamicDao>()
		val session = mock<ClientSession>()
		val execChannel = mock<ChannelExec>()
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { NullInputStream(0) }
		whenever(execChannel.open()).thenReturn(mock())


		whenever(hostExecutor.execute(eq(testHost), any<(ClientSession) -> Unit>())).then {
			(it.arguments[1] as (ClientSession) -> Unit).invoke(session)
		}

		UnAllocateLvExecutor(hostExecutor, vssDynDao).execute(
				UnAllocateLv(
						host = testHost,
						allocation = VirtualStorageLvmAllocation(
								hostId = testHost.id,
								path = "/dev/kerub/test",
								actualSize = 100.GB
						),
						vstorage = testDisk
				)
		)

		//TODO verify(vssDynDao).update(eq(testHost.id), retrieve = any<(UUID) -> VirtualStorageDeviceDynamic>(), change = any<(VirtualStorageDeviceDynamic) -> VirtualStorageDeviceDynamic>())
	}
}