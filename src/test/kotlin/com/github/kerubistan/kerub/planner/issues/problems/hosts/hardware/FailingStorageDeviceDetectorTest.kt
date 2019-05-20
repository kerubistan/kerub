package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.common.AbstractProblemDetectorVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import kotlin.test.assertTrue

class FailingStorageDeviceDetectorTest : AbstractProblemDetectorVerifications(FailingStorageDeviceDetector){

	@Test
	fun detect() {
		assertTrue("there are no failed storage devices") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									LvmStorageCapability(
											volumeGroupName = "test-vg",
											size = 1.TB,
											physicalVolumes = mapOf("/dev/sda" to 1.TB))
							),
							blockDevices = listOf(
									BlockDevice(
											deviceName = "/dev/sda",
											storageCapacity = 1.TB)
							)
					)
			)
			FailingStorageDeviceDetector.detect(
					Plan(
							OperationalState.fromLists(
									hosts = listOf(host),
									hostDyns = listOf(
											hostUp(host).copy(
													storageDeviceHealth = mapOf("/dev/sda" to true)
											))
							))).isEmpty()
		}
		assertTrue("a disk failed - must be detected") {
			val lvmCapability = LvmStorageCapability(
					volumeGroupName = "test-vg",
					size = 1.TB,
					physicalVolumes = mapOf("/dev/sda" to 1.TB))
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(
									lvmCapability,
									LvmStorageCapability(
											volumeGroupName = "other-vg",
											physicalVolumes = mapOf("/dev/sdb" to 1.TB, "/dev/sdc" to 1.TB),
											size = 2.TB
									)
							),
							blockDevices = listOf(
									BlockDevice(
											deviceName = "/dev/sda",
											storageCapacity = 1.TB),
									BlockDevice(
											deviceName = "/dev/sdb",
											storageCapacity = 1.TB),
									BlockDevice(
											deviceName = "/dev/sdc",
											storageCapacity = 1.TB)
							)
					)
			)
			FailingStorageDeviceDetector.detect(
					Plan(
							OperationalState.fromLists(
									hosts = listOf(host),
									hostDyns = listOf(
											hostUp(host).copy(
													storageDeviceHealth = mapOf("/dev/sda" to false)
											))
							))).single() == FailingStorageDevice(
					storageCapability = lvmCapability,
					host = host,
					device = "/dev/sda")
		}

	}
}