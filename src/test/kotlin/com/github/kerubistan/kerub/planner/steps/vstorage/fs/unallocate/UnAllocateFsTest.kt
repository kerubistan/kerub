package com.github.kerubistan.kerub.planner.steps.vstorage.fs.unallocate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class UnAllocateFsTest {

	@Test
	fun take() {
		val allocation = VirtualStorageFsAllocation(
				type = VirtualDiskFormat.qcow2,
				fileName = "test.qcow2",
				mountPoint = "/mnt",
				hostId = testHost.id,
				actualSize = 10.GB
		)
		val updatedState = UnAllocateFs(
				vstorage = testDisk,
				allocation = allocation,
				host = testHost
		).take(
				OperationalState.fromLists(
						vStorage = listOf(testDisk),
						vStorageDyns = listOf(
								VirtualStorageDeviceDynamic(
										id = testDisk.id,
										allocations = listOf(allocation)
								)
						),
						hosts = listOf(testHost),
						hostDyns = listOf(
								HostDynamic(id = testHost.id, status = HostStatus.Up)
						)
				)
		)

		assertTrue(updatedState.vStorage[testDisk.id]!!.dynamic!!.allocations.isEmpty())
	}
}