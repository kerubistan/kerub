package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.toInputStream
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

class CreateThinLvExecutorTest {
	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val virtualDiskDynDao = mock<VirtualStorageDeviceDynamicDao>()
		val session = mock<ClientSession>()
		val future = mock<OpenFuture>()

		val firstListChannel = mock<ChannelExec>()
		whenever(firstListChannel.invertedErr).then { NullInputStream(0) }
		whenever(
				firstListChannel.invertedOut).then { """  eCuTKA-rIDz-dzJq-48pK-DtqJ-X77p-YStcLS:testlv1:/dev/test/testlv1:1073741824B:::linear:""".toInputStream() }
		whenever(firstListChannel.open()).thenReturn(future)

		val secondListChannel = mock<ChannelExec>()
		whenever(secondListChannel.invertedErr).then { NullInputStream(0) }
		whenever(secondListChannel.invertedOut).then {
			"""  eCuTKA-rIDz-dzJq-48pK-DtqJ-X77p-YStcLS:testlv1:/dev/test/testlv1:1073741824B:::linear:
			|  eCuTKA-rIDz-dzJq-48pK-DtqJ-X77p-YStcLA:${testDisk.id}:/dev/test/${testDisk.id}:1073741824B:::linear:
		""".trimMargin().toInputStream()
		}
		whenever(secondListChannel.open()).thenReturn(future)

		whenever(session.createExecChannel(argThat { this.startsWith("lvm lvs") })).thenReturn(firstListChannel,
																							   secondListChannel)

		val createLvChannel = mock<ChannelExec>()
		whenever(session.createExecChannel(argThat { this.startsWith("lvm lvcreate") })).thenReturn(createLvChannel)
		whenever(createLvChannel.invertedErr).then { NullInputStream(0) }
		whenever(createLvChannel.invertedOut).then { NullInputStream(0) }
		whenever(createLvChannel.open()).thenReturn(future)


		whenever(hostCommandExecutor.execute(eq(testHost), any<(ClientSession) -> Any>())).then {
			(it.arguments[1] as ((ClientSession) -> Any))(session)
		}

		CreateThinLvExecutor(hostCommandExecutor, virtualDiskDynDao).execute(
				CreateThinLv(
						host = testHost,
						poolName = "pool-1",
						volumeGroupName = "vg-1",
						disk = testDisk
				)
		)

		verify(virtualDiskDynDao).add(argThat {
			println(this)
			this is VirtualStorageDeviceDynamic
					&& this.allocations.any { it is VirtualStorageLvmAllocation }
		})
	}

}