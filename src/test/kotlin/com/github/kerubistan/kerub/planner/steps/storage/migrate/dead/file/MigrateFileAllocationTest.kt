package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFs
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID
import kotlin.test.assertTrue

class MigrateFileAllocationTest {

	@Test
	fun validate() {
		assertThrows<IllegalStateException>("source allocation type different than the target") {
			val sourceCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					fsType = "ext4",
					size = 1.TB
			)
			val targetCapability = FsStorageCapability(
					id = randomUUID(),
					mountPoint = "/kerub",
					fsType = "ext4",
					size = 1.TB
			)
			val sourceHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(sourceCapability)
					)
			)
			val targetHost = testHost.copy(
					id = randomUUID(),
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(targetCapability)
					)
			)
			val sourceAllocation = VirtualStorageFsAllocation(
					capabilityId = sourceCapability.id,
					mountPoint = sourceCapability.mountPoint,
					type = VirtualDiskFormat.qcow2,
					fileName = "${testDisk.id}.qcow2",
					actualSize = testDisk.size,
					hostId = sourceHost.id
			)
			MigrateFileAllocation(
					sourceHost = sourceHost,
					targetHost = targetHost,
					sourceAllocation = sourceAllocation,
					virtualStorage = testDisk,
					allocationStep = CreateImage(
							host = targetHost,
							disk = testDisk,
							capability = targetCapability,
							format = VirtualDiskFormat.qed
					),
					deAllocationStep = UnAllocateFs(
							vstorage = testDisk,
							host = sourceHost,
							allocation = sourceAllocation
					)
			)
		}
	}

	@Test
	fun take() {
		val sourceCapability = FsStorageCapability(
				id = randomUUID(),
				mountPoint = "/kerub",
				fsType = "ext4",
				size = 1.TB
		)
		val targetCapability = FsStorageCapability(
				id = randomUUID(),
				mountPoint = "/kerub",
				fsType = "ext4",
				size = 1.TB
		)
		val sourceHost = testHost.copy(
				id = randomUUID(),
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(sourceCapability)
				)
		)
		val targetHost = testHost.copy(
				id = randomUUID(),
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(targetCapability)
				)
		)
		val sourceAllocation = VirtualStorageFsAllocation(
				capabilityId = sourceCapability.id,
				mountPoint = sourceCapability.mountPoint,
				type = VirtualDiskFormat.qcow2,
				fileName = "${testDisk.id}.qcow2",
				actualSize = testDisk.size,
				hostId = sourceHost.id
		)
		val sourceHostDyn = hostUp(sourceHost).copy(
				storageStatus = listOf(SimpleStorageDeviceDynamic(id = sourceCapability.id, freeCapacity = 500.GB))
		)
		val targetHostDyn = hostUp(targetHost).copy(
				storageStatus = listOf(SimpleStorageDeviceDynamic(id = targetCapability.id, freeCapacity = 800.GB))
		)
		val newState = MigrateFileAllocation(
				sourceHost = sourceHost,
				targetHost = targetHost,
				sourceAllocation = sourceAllocation,
				virtualStorage = testDisk,
				allocationStep = CreateImage(
						host = targetHost,
						disk = testDisk,
						capability = targetCapability,
						format = VirtualDiskFormat.qcow2
				),
				deAllocationStep = UnAllocateFs(
						vstorage = testDisk,
						host = sourceHost,
						allocation = sourceAllocation
				)
		).take(
				OperationalState.fromLists(
						hosts = listOf(sourceHost, targetHost),
						hostDyns = listOf(sourceHostDyn, targetHostDyn),
						vStorage = listOf(testDisk),
						vStorageDyns = listOf(
								VirtualStorageDeviceDynamic(
										id = testDisk.id,
										allocations = listOf(sourceAllocation)
								)
						)
				)
		)

		assertTrue("allocation must move to the host") {
			newState.vStorage.getValue(
					testDisk.id).dynamic!!.allocations.single().hostId == targetHost.id
		}
	}
}