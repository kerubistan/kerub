package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.othercap

import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVirtualDisk
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class ConvertImageTest : OperationalStepVerifications() {
	override val step: AbstractOperationalStep
		get()  {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			val targetAllocation = VirtualStorageFsAllocation(
					hostId = host.id,
					type = VirtualDiskFormat.qcow2,
					fileName = "/kerub-2/${testVirtualDisk.id}.qcow2",
					mountPoint = "/kerub-2",
					actualSize = 1.GB,
					capabilityId = testFsCapability.id
			)
			val sourceAllocation = VirtualStorageFsAllocation(
					hostId = host.id,
					type = VirtualDiskFormat.raw,
					fileName = "/kerub-1/${testVirtualDisk.id}.raw",
					mountPoint = "/kerub-1",
					actualSize = 1.GB,
					capabilityId = testFsCapability.id
			)
			return ConvertImage(
					sourceAllocation  = sourceAllocation,
					targetAllocation = targetAllocation,
					host = host,
					virtualStorage = testVirtualDisk
			)
		}

	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			ConvertImage(
					sourceAllocation = VirtualStorageFsAllocation(
							hostId = testHost.id,
							type = VirtualDiskFormat.qcow2,
							fileName = "/kerub/${testDisk.id}.qcow2",
							mountPoint = "/kerub",
							actualSize = 1.GB,
							capabilityId = testFsCapability.id
					),
					targetAllocation = VirtualStorageFsAllocation(
							hostId = testOtherHost.id,
							type = VirtualDiskFormat.raw,
							fileName = "/kerub/${testDisk.id}.raw",
							mountPoint = "/kerub",
							actualSize = 1.GB,
							capabilityId = testFsCapability.id
					),
					host = testHost,
					virtualStorage = testVirtualDisk
			)
		}
		// can not just pipe the output through ssh since, qemu-img won't write to stdout, it does lots of seeks
		assertThrows<IllegalStateException> ("allocation host different than source host") {
			ConvertImage(
					targetAllocation = VirtualStorageFsAllocation(
							hostId = testHost.id,
							type = VirtualDiskFormat.qcow2,
							fileName = "/kerub/${testDisk.id}.qcow2",
							mountPoint = "/kerub",
							actualSize = 1.GB,
							capabilityId = testFsCapability.id
					),
					sourceAllocation  = VirtualStorageFsAllocation(
							hostId = testOtherHost.id,
							type = VirtualDiskFormat.raw,
							fileName = "/kerub/${testDisk.id}.raw",
							mountPoint = "/kerub",
							actualSize = 1.GB,
							capabilityId = testFsCapability.id
					),
					host = testHost,
					virtualStorage = testVirtualDisk
			)
		}
	}

	@Test
	fun take() {
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(testFsCapability)
				)
		)
		val targetAllocation = VirtualStorageFsAllocation(
				hostId = host.id,
				type = VirtualDiskFormat.qcow2,
				fileName = "/kerub-2/${testVirtualDisk.id}.qcow2",
				mountPoint = "/kerub-2",
				actualSize = 1.GB,
				capabilityId = testFsCapability.id
		)
		val sourceAllocation = VirtualStorageFsAllocation(
				hostId = host.id,
				type = VirtualDiskFormat.raw,
				fileName = "/kerub-1/${testVirtualDisk.id}.raw",
				mountPoint = "/kerub-1",
				actualSize = 1.GB,
				capabilityId = testFsCapability.id
		)
		val updatedState = ConvertImage(
				sourceAllocation  = sourceAllocation,
				targetAllocation = targetAllocation,
				host = host,
				virtualStorage = testVirtualDisk
		).take(
				OperationalState.fromLists(
						hosts = listOf(host),
						hostDyns = listOf(HostDynamic(
								id = host.id,
								storageStatus = listOf(
										SimpleStorageDeviceDynamic(
												id = testFsCapability.id,
												freeCapacity = testFsCapability.size
										)
								)
						)),
						vStorage = listOf(testVirtualDisk),
						vStorageDyns = listOf(
								VirtualStorageDeviceDynamic(
										id = testVirtualDisk.id,
										allocations = listOf(
												sourceAllocation
										)
								)
						)
				)
		)

		assertTrue { updatedState.vStorage[testVirtualDisk.id]?.dynamic?.allocations?.single() == targetAllocation }

	}
}