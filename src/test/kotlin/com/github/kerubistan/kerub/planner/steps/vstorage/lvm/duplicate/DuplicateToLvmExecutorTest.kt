package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.duplicate

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.jupiter.api.Test

internal class DuplicateToLvmExecutorTest {

	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()

		val virtualStorageDynamicDao = mock<VirtualStorageDeviceDynamicDao>()

		DuplicateToLvmExecutor(hostCommandExecutor, virtualStorageDynamicDao).execute(
				DuplicateToLvm(
						sourceHost = testHost,
						source = VirtualStorageLvmAllocation(
							vgName = "source-vg",
								actualSize = testDisk.size,
								path = "/dev/source-vg/${testDisk.id}",
								hostId = testHost.id
						),
						targetHost = testOtherHost,
						target = VirtualStorageLvmAllocation(
								vgName = "target-vg",
								actualSize = testDisk.size,
								path = "/dev/target-vg/${testDisk.id}",
								hostId = testHost.id
						),
						vStorageDevice = testDisk
				)
		)

		verify(virtualStorageDynamicDao).update(
				id = eq(testDisk.id),
				retrieve = any(),
				change = any()
		)
	}
}