package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.inplace

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertEquals

class InPlaceConvertImageExecutorTest {

	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val virtualStorageDynamicDao = mock<VirtualStorageDeviceDynamicDao>()
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								testFsCapability
						)
				)
		)
		val session = mock<ClientSession>()
		session.mockCommandExecution("qemu-img convert .*".toRegex())
		session.mockCommandExecution("qemu-img check .*".toRegex())
		session.mockCommandExecution("du .*".toRegex(), "123456	thatfile")
		session.mockCommandExecution("rm .*".toRegex())
		val fsAllocation = VirtualStorageFsAllocation(
				capabilityId = testFsCapability.id,
				mountPoint = testFsCapability.mountPoint,
				fileName = "${testFsCapability.mountPoint}/${testDisk.id}.raw",
				type = VirtualDiskFormat.raw,
				actualSize = testDisk.size,
				hostId = host.id
		)
		var dyn = VirtualStorageDeviceDynamic(
				id = testDisk.id,
				allocations = listOf(fsAllocation)
		)
		doAnswer {
			(it.arguments[1] as (ClientSession) -> Any).invoke(session)
		}.whenever(hostCommandExecutor).execute(eq(host), any<(ClientSession) -> Any>())
		doAnswer {
			dyn = (it.arguments[2] as (VirtualStorageDeviceDynamic) -> VirtualStorageDeviceDynamic).invoke(dyn)
		}.whenever(virtualStorageDynamicDao).update(eq(testDisk.id), any(), any())
		InPlaceConvertImageExecutor(hostCommandExecutor, virtualStorageDynamicDao).execute(
				InPlaceConvertImage(
						host = host,
						targetFormat = VirtualDiskFormat.qcow2,
						sourceAllocation = fsAllocation,
						virtualStorage = testDisk
				)
		)

		assertEquals(123456, dyn.allocations.single().actualSize.toInt())
		assertEquals(VirtualDiskFormat.qcow2, (dyn.allocations.single() as VirtualStorageFsAllocation).type)
	}
}