package com.github.kerubistan.kerub.planner.steps.vstorage.remove

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import kotlin.test.assertTrue

class RemoveVirtualStorageFactoryTest {

	@Test
	fun produce() {
		assertTrue("blank state, no operation") {
			RemoveVirtualStorageFactory.produce(OperationalState.fromLists()).isEmpty()
		}
		assertTrue("no recycling vstorage, no operation") {
			RemoveVirtualStorageFactory.produce(OperationalState.fromLists(vStorage = listOf(testDisk))).isEmpty()
		}
		assertTrue("recycling vstorage but with allocations, no operation") {
			RemoveVirtualStorageFactory.produce(
					OperationalState.fromLists(
							vStorage = listOf(testDisk.copy(recycling = true)),
							vStorageDyns = listOf(VirtualStorageDeviceDynamic(
									id = testDisk.id,
									allocations = listOf(
											VirtualStorageFsAllocation(
													hostId = testHost.id,
													type = VirtualDiskFormat.qcow2,
													fileName = "blah.qcow2",
													mountPoint = "/mnt",
													actualSize = 100.GB,
													capabilityId = testFsCapability.id
											)
									)
							))
					)
			).isEmpty()
		}
		assertTrue("recycling vstorage with no more allocations - let's remove it") {
			val recyclingDisk = testDisk.copy(recycling = true)
			RemoveVirtualStorageFactory.produce(
					OperationalState.fromLists(
							vStorage = listOf(recyclingDisk),
							vStorageDyns = listOf(VirtualStorageDeviceDynamic(
									id = recyclingDisk.id,
									allocations = listOf()
							))
					)
			) == listOf(RemoveVirtualStorage(recyclingDisk))
		}

	}
}