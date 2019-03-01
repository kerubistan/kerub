package com.github.kerubistan.kerub.planner.steps.storage.fs.fallocate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertTrue

class FallocateImageTest {

	@Test
	fun take() {
		val capability = FsStorageCapability(
				size = 1.TB,
				mountPoint = "/kerub",
				fsType = "ext4"
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(capability)
				)
		)
		val hostDynamic = hostUp(host).copy(
				storageStatus = listOf(
						SimpleStorageDeviceDynamic(
								id = capability.id,
								freeCapacity = 500.GB
						)
				)
		)
		val newState = FallocateImage(
				host = host,
				virtualStorage = testDisk,
				allocation = VirtualStorageFsAllocation(
						hostId = host.id,
						actualSize = 10.GB,
						capabilityId = capability.id,
						mountPoint = "/kerub",
						type = VirtualDiskFormat.raw,
						fileName = "blah.raw"
				),
				expectedFree = 1.GB
		).take(
				OperationalState.fromLists(
						hosts = listOf(host),
						hostDyns = listOf(hostDynamic),
						vStorage = listOf(testDisk),
						vStorageDyns = listOf(
								VirtualStorageDeviceDynamic(
										id = testDisk.id,
										allocations = listOf(
												VirtualStorageFsAllocation(
														capabilityId = capability.id,
														fileName = "test.qcow2",
														type = VirtualDiskFormat.qcow2,
														mountPoint = capability.mountPoint,
														actualSize = 10.GB,
														hostId = host.id
												)
										)
								)
						)
				)
		)

		assertTrue(newState.hosts.getValue(host.id).dynamic!!.storageStatus.single().freeCapacity > 500.GB)
		assertTrue(
				(newState.vStorage.getValue(testDisk.id).dynamic!!.allocations.single() as VirtualStorageFsAllocation)
						.actualSize < 10.GB
		)

	}
}