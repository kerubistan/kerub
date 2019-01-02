package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue
import kotlin.test.expect

class VirtualStorageAllocationOnFailingStorageDeviceDetectorTest {

	@Test
	fun detect() {
		expect(listOf(), "blank state - no problem") {
			VirtualStorageAllocationOnFailingStorageDeviceDetector.detect(
					Plan(OperationalState.fromLists())
			)
		}
		assertTrue("virtual disk on failing capability") {
			val dieingCapability = testLvmCapability.copy(
					id = UUID.randomUUID(),
					volumeGroupName = "dieing-vg",
					size = 1.TB,
					physicalVolumes = mapOf(
							"/dev/sdb" to 1.TB
					)
			)
			val healthyCapability = testLvmCapability.copy(
					id = UUID.randomUUID(),
					volumeGroupName = "healthy-vg",
					size = 1.TB,
					physicalVolumes = mapOf(
							"/dev/sdc" to 1.TB
					)
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									healthyCapability,
									dieingCapability
							)
					)
			)
			val hostDynamic = hostUp(host).copy(
					storageDeviceHealth = mapOf(
							"/dev/sdb" to false,
							"/dev/sdc" to true
					)
			)
			val allocationOnFailingVg = VirtualStorageLvmAllocation(
					hostId = host.id,
					path = "",
					capabilityId = dieingCapability.id,
					actualSize = 2.GB,
					vgName = dieingCapability.volumeGroupName
			)
			VirtualStorageAllocationOnFailingStorageDeviceDetector.detect(
					Plan(OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDynamic),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													allocationOnFailingVg
											)
									)
							)
					))
			) == listOf(
					VirtualStorageAllocationOnFailingStorageDevice(
							host = host,
							allocation = allocationOnFailingVg,
							capability = dieingCapability,
							storageDevice = testDisk
					)
			)
		}
	}
}