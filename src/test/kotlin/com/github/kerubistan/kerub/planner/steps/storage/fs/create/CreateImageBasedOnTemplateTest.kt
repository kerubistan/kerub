package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testCdrom
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import io.github.kerubistan.kroki.size.GB
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

internal class CreateImageBasedOnTemplateTest {

	@Test
	fun validations() {
		assertThrows<IllegalArgumentException>("base allocation must be on the same host") {
			CreateImageBasedOnTemplate(
					format = VirtualDiskFormat.qcow2,
					disk = testDisk,
					capability = testFsCapability,
					host = testHost,
					baseAllocation = VirtualStorageFsAllocation(
							hostId = testOtherHost.id, // intentionally wrong
							actualSize = 100.GB,
							capabilityId = testFsCapability.id,
							mountPoint = "/kerub",
							fileName = "${testDisk.id}.qcow2",
							type = VirtualDiskFormat.qcow2
					),
					baseDisk = testCdrom
			)
		}
		assertThrows<IllegalArgumentException>("format must be qcow2, qed, vmdk - not raw") {
			CreateImageBasedOnTemplate(
					format = VirtualDiskFormat.raw,
					disk = testDisk,
					capability = testFsCapability,
					host = testHost,
					baseAllocation = VirtualStorageFsAllocation(
							hostId = testHost.id,
							actualSize = 100.GB,
							capabilityId = testFsCapability.id,
							mountPoint = "/kerub",
							fileName = "${testDisk.id}.qcow2",
							type = VirtualDiskFormat.qcow2
					),
					baseDisk = testCdrom
			)
		}

		assertThrows<IllegalArgumentException>("format must be qcow2, qed, vmdk - not vdi") {
			CreateImageBasedOnTemplate(
					format = VirtualDiskFormat.vdi,
					disk = testDisk,
					capability = testFsCapability,
					host = testHost,
					baseAllocation = VirtualStorageFsAllocation(
							hostId = testHost.id,
							actualSize = 100.GB,
							capabilityId = testFsCapability.id,
							mountPoint = "/kerub",
							fileName = "${testDisk.id}.qcow2",
							type = VirtualDiskFormat.qcow2
					),
					baseDisk = testCdrom
			)
		}

		assertThrows<IllegalArgumentException>("the template can't be the same as the target disk") {
			CreateImageBasedOnTemplate(
					format = VirtualDiskFormat.qcow2,
					disk = testDisk,
					capability = testFsCapability,
					host = testHost,
					baseAllocation = VirtualStorageFsAllocation(
							hostId = testHost.id,
							actualSize = 100.GB,
							capabilityId = testFsCapability.id,
							mountPoint = "/kerub",
							fileName = "${testDisk.id}.qcow2",
							type = VirtualDiskFormat.qcow2
					),
					baseDisk = testDisk
			)
		}

		assertThrows<IllegalArgumentException>("the formats must be the same") {
			CreateImageBasedOnTemplate(
					format = VirtualDiskFormat.qed,
					disk = testDisk,
					capability = testFsCapability,
					host = testHost,
					baseAllocation = VirtualStorageFsAllocation(
							hostId = testHost.id,
							actualSize = 100.GB,
							capabilityId = testFsCapability.id,
							mountPoint = "/kerub",
							fileName = "${testDisk.id}.qcow2",
							type = VirtualDiskFormat.qcow2
					),
					baseDisk = testCdrom
			)
		}

	}

	@Test
	fun take() {
		val state = CreateImageBasedOnTemplate(
				format = VirtualDiskFormat.qcow2,
				disk = testDisk,
				capability = testFsCapability,
				host = testHost,
				baseAllocation = VirtualStorageFsAllocation(
						hostId = testHost.id,
						actualSize = 100.GB,
						capabilityId = testFsCapability.id,
						mountPoint = "/kerub",
						fileName = "/kerub/${testDisk.id}.qcow2",
						type = VirtualDiskFormat.qcow2
				),
				baseDisk = testCdrom
		).take(
				OperationalState.fromLists(
						hosts = listOf(testHost),
						hostDyns = listOf(hostUp(testHost)),
						vStorage = listOf(testDisk, testCdrom),
						vStorageDyns = listOf(
								VirtualStorageDeviceDynamic(
										id = testCdrom.id,
										allocations = listOf(
												VirtualStorageFsAllocation(
													capabilityId = testFsCapability.id,
														hostId = testHost.id,
														type = VirtualDiskFormat.qcow2,
														fileName =
														"${testFsCapability.mountPoint}/${testCdrom.id}.qcow2",
														actualSize = 1.GB,
														mountPoint = testFsCapability.mountPoint
												)
										)
								)
						)
				)
		)

		assertTrue("storage must be allocated on test host") {
			state.vStorage[testDisk.id]!!.dynamic!!.allocations.single().hostId == testHost.id
		}
	}
}