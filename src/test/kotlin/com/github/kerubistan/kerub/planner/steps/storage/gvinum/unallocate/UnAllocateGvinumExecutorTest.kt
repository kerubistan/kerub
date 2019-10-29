package com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testGvinumCapability
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.session.ClientSession
import org.junit.Test

class UnAllocateGvinumExecutorTest {

	@Test
	fun execute() {
		val hostExecutor = mock<HostCommandExecutor>()
		val vssDynDao = mock<VirtualStorageDeviceDynamicDao>()
		val session = mock<ClientSession>()
		val execChannel = mock<ChannelExec>()

		hostExecutor.mockHost(testFreeBsdHost, session)
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut).then { NullInputStream(0) }
		whenever(execChannel.open()).thenReturn(mock())

		UnAllocateGvinumExecutor(hostExecutor, vssDynDao).execute(
				UnAllocateGvinum(
						vstorage = testDisk,
						host = testFreeBsdHost,
						allocation = VirtualStorageGvinumAllocation(
								actualSize = 10.GB,
								hostId = testFreeBsdHost.id,
								configuration = SimpleGvinumConfiguration(
										diskName = "gvinum-disk-1"
								),
								capabilityId = testGvinumCapability.id
						)
				)
		)

		verify(session).createExecChannel(argThat { startsWith("gvinum rm ") })

	}
}