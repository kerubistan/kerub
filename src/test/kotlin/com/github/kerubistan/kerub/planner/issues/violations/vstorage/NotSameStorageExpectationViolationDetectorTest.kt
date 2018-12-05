package com.github.kerubistan.kerub.planner.issues.violations.vstorage

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotSameStorageExpectationViolationDetectorTest {
	@Test
	fun check() {

		assertTrue("blank state should be fine") {
			NotSameStorageExpectationViolationDetector
					.check(testDisk, NotSameStorageExpectation(otherDiskId = testDisk.id), OperationalState.fromLists())
		}

		assertTrue("other disk not allocated yet") {
			val secondDisk = testDisk.copy(
					id = UUID.randomUUID(),
					expectations = listOf()
			)
			val notSameStorageExpectation = NotSameStorageExpectation(otherDiskId = secondDisk.id)
			val firstDisk = testDisk.copy(
					id = UUID.randomUUID(),
					expectations = listOf(notSameStorageExpectation)
			)
			NotSameStorageExpectationViolationDetector.check(
					firstDisk,
					notSameStorageExpectation,
					OperationalState.fromLists(
							vStorage = listOf(firstDisk, secondDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = firstDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															actualSize = 10.GB,
															fileName = "first-disk.raw",
															type = VirtualDiskFormat.raw,
															mountPoint = testFsCapability.mountPoint,
															hostId = testHost.id,
															capabilityId = testFsCapability.id
													)
											)
									),
									VirtualStorageDeviceDynamic(
											id = secondDisk.id,
											allocations = listOf()
									)
							),
							hosts = listOf(testHost)
					)
			)
		}

		assertTrue("constrained disk not allocated yet") {
			val secondDisk = testDisk.copy(
					id = UUID.randomUUID(),
					expectations = listOf()
			)
			val notSameStorageExpectation = NotSameStorageExpectation(otherDiskId = secondDisk.id)
			val firstDisk = testDisk.copy(
					id = UUID.randomUUID(),
					expectations = listOf(notSameStorageExpectation)
			)
			NotSameStorageExpectationViolationDetector.check(
					firstDisk,
					notSameStorageExpectation,
					OperationalState.fromLists(
							vStorage = listOf(firstDisk, secondDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = firstDisk.id,
											allocations = listOf(
											)
									),
									VirtualStorageDeviceDynamic(
											id = secondDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															actualSize = 10.GB,
															fileName = "second-disk.raw",
															type = VirtualDiskFormat.raw,
															mountPoint = testFsCapability.mountPoint,
															hostId = testHost.id,
															capabilityId = testFsCapability.id
													)
											)
									)
							),
							hosts = listOf(testHost)
					)
			)
		}

		assertFalse("disks on the same host") {
			val secondDisk = testDisk.copy(
					id = UUID.randomUUID(),
					expectations = listOf()
			)
			val notSameStorageExpectation = NotSameStorageExpectation(otherDiskId = secondDisk.id)
			val firstDisk = testDisk.copy(
					id = UUID.randomUUID(),
					expectations = listOf(notSameStorageExpectation)
			)
			NotSameStorageExpectationViolationDetector.check(
					firstDisk,
					notSameStorageExpectation,
					OperationalState.fromLists(
							vStorage = listOf(firstDisk, secondDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = firstDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															actualSize = 10.GB,
															fileName = "first-disk.raw",
															type = VirtualDiskFormat.raw,
															mountPoint = testFsCapability.mountPoint,
															hostId = testHost.id,
															capabilityId = testFsCapability.id
													)
											)
									),
									VirtualStorageDeviceDynamic(
											id = secondDisk.id,
											allocations = listOf(
													VirtualStorageFsAllocation(
															actualSize = 10.GB,
															fileName = "second-disk.qcow2",
															type = VirtualDiskFormat.qcow2,
															mountPoint = testFsCapability.mountPoint,
															hostId = testHost.id,
															capabilityId = testFsCapability.id
													)
											)
									)
							),
							hosts = listOf(testHost)
					)
			)
		}
	}

}