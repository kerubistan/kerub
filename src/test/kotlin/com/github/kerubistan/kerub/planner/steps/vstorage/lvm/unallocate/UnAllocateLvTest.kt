package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.unallocate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.Test

class UnAllocateLvTest {

	@Test
	fun take() {
		val allocation = VirtualStorageLvmAllocation(
				hostId = testHost.id,
				actualSize = 10.GB,
				path = "/dev/kerub/test",
				vgName = "kerub",
				capabilityId = testLvmCapability.id
		)
		val updatedState = UnAllocateLv(
				vstorage = testDisk,
				allocation = allocation,
				host = testHost
		).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostDyns = listOf(
								HostDynamic(
										id = testHost.id,
										status = HostStatus.Up
								)
						),
						vStorage = listOf(testDisk),
						vStorageDyns = listOf(
								VirtualStorageDeviceDynamic(
										id = testDisk.id,
										allocations = listOf(allocation)
								)
						)
				)
		)

		assertTrue(updatedState.vStorage[testDisk.id]!!.dynamic!!.allocations.isEmpty())
	}
}