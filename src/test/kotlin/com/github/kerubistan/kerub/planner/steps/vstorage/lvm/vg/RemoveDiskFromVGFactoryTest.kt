package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.vg

import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.expect

class RemoveDiskFromVGFactoryTest {

	@Test
	fun produce() {
		expect(listOf(), "blank state - no steps offered") {
			RemoveDiskFromVGFactory.produce(OperationalState.fromLists())
		}
		expect(listOf(), "All disks healthy - let's not remove them") {
			val sda = BlockDevice(deviceName = "/dev/sda", storageCapacity = 4.TB)
			val sdb = BlockDevice(deviceName = "/dev/sdb", storageCapacity = 4.TB)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									LvmStorageCapability(
											volumeGroupName = "test-vg",
											size = 8.TB,
											physicalVolumes = mapOf(
													sda.deviceName to sda.storageCapacity,
													sdb.deviceName to sdb.storageCapacity
											))),
							blockDevices = listOf(sda, sdb)
					)
			)
			val hostDynamic = hostUp(host).copy(
					storageDeviceHealth = mapOf(
							sda.deviceName to true,
							sdb.deviceName to true
					)
			)
			RemoveDiskFromVGFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDynamic)
					))
		}

		expect(listOf(), "One disk fails - but not enough capacity free to remove it") {
			val sda = BlockDevice(deviceName = "/dev/sda", storageCapacity = 4.TB)
			val sdb = BlockDevice(deviceName = "/dev/sdb", storageCapacity = 4.TB)
			val lvmStorageCapability = LvmStorageCapability(
					volumeGroupName = "test-vg",
					size = 8.TB,
					physicalVolumes = mapOf(
							sda.deviceName to sda.storageCapacity,
							sdb.deviceName to sdb.storageCapacity
					))
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									lvmStorageCapability),
							blockDevices = listOf(sda, sdb)
					)
			)
			val hostDynamic = hostUp(host).copy(
					storageDeviceHealth = mapOf(
							sda.deviceName to true,
							sdb.deviceName to false
					),
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 1.TB
							)
					)
			)
			RemoveDiskFromVGFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDynamic)
					))
		}

		assertTrue("One disk fails - but not enough capacity free to remove it") {
			val sda = BlockDevice(deviceName = "/dev/sda", storageCapacity = 4.TB)
			val sdb = BlockDevice(deviceName = "/dev/sdb", storageCapacity = 4.TB)
			val lvmStorageCapability = LvmStorageCapability(
					volumeGroupName = "test-vg",
					size = 8.TB,
					physicalVolumes = mapOf(
							sda.deviceName to sda.storageCapacity,
							sdb.deviceName to sdb.storageCapacity
					))
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									lvmStorageCapability),
							blockDevices = listOf(sda, sdb)
					)
			)
			val hostDynamic = hostUp(host).copy(
					storageDeviceHealth = mapOf(
							sda.deviceName to true,
							sdb.deviceName to false
					),
					storageStatus = listOf(
							SimpleStorageDeviceDynamic(
									id = lvmStorageCapability.id,
									freeCapacity = 7.TB
							)
					)
			)
			RemoveDiskFromVGFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(hostDynamic)
					)) == listOf(RemoveDiskFromVG(
							capability = lvmStorageCapability,
							device = sdb.deviceName,
							host = host))
		}


	}
}