package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.inplace

import com.github.kerubistan.kerub.hostUp
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
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class InPlaceConvertImageTest : OperationalStepVerifications() {
	override val step: AbstractOperationalStep
		get() {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			return InPlaceConvertImage(
					testDisk,
					VirtualStorageFsAllocation(
							capabilityId = testFsCapability.id,
							mountPoint = "/kerub",
							hostId = host.id,
							type = VirtualDiskFormat.qcow2,
							fileName = "/kerub/${testDisk.id}.qcow2",
							actualSize = testDisk.size
					),
					host,
					VirtualDiskFormat.raw
			)
		}

	@Test
	fun validation() {
		assertThrows<IllegalStateException>(
				"should get exception when the allocation host is not the same as the host") {
			InPlaceConvertImage(
					testDisk,
					VirtualStorageFsAllocation(
							capabilityId = testFsCapability.id,
							mountPoint = "/kerub",
							hostId = testOtherHost.id,
							type = VirtualDiskFormat.qcow2,
							fileName = "/kerub/${testDisk.id}.qcow2",
							actualSize = testDisk.size
					),
					testHost,
					VirtualDiskFormat.raw)
		}
		assertThrows<IllegalStateException>("capability not registered in the host, should throw exception") {
			InPlaceConvertImage(
					testDisk,
					VirtualStorageFsAllocation(
							capabilityId = testFsCapability.id,
							mountPoint = "/kerub",
							hostId = testHost.id,
							type = VirtualDiskFormat.qcow2,
							fileName = "/kerub/${testDisk.id}.qcow2",
							actualSize = testDisk.size
					),
					testHost,
					VirtualDiskFormat.raw)
		}

		assertThrows<IllegalStateException>("same format for source and target - does not make sense") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			InPlaceConvertImage(
					testDisk,
					VirtualStorageFsAllocation(
							capabilityId = testFsCapability.id,
							mountPoint = "/kerub",
							hostId = host.id,
							type = VirtualDiskFormat.qcow2,
							fileName = "/kerub/${testDisk.id}.qcow2",
							actualSize = testDisk.size
					),
					host,
					VirtualDiskFormat.qcow2)
		}


		//check that this passes
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(testFsCapability)
				)
		)
		InPlaceConvertImage(
				testDisk,
				VirtualStorageFsAllocation(
						capabilityId = testFsCapability.id,
						mountPoint = "/kerub",
						hostId = host.id,
						type = VirtualDiskFormat.qcow2,
						fileName = "/kerub/${testDisk.id}.qcow2",
						actualSize = testDisk.size
				),
				host,
				VirtualDiskFormat.raw)

	}

	@Test
	fun take() {
		assertTrue("") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							storageCapabilities = listOf(testFsCapability)
					)
			)
			val sourceAllocation = VirtualStorageFsAllocation(
					capabilityId = testFsCapability.id,
					mountPoint = "/kerub",
					hostId = host.id,
					type = VirtualDiskFormat.qcow2,
					fileName = "/kerub/${testDisk.id}.qcow2",
					actualSize = testDisk.size
			)
			val state = InPlaceConvertImage(testDisk, sourceAllocation, host, VirtualDiskFormat.raw).take(OperationalState.fromLists(
					hosts = listOf(host),
					hostDyns = listOf(hostUp(host)),
					vStorage = listOf(testDisk),
					vStorageDyns = listOf(
							VirtualStorageDeviceDynamic(
									id = testDisk.id,
									allocations = listOf(sourceAllocation)
							)
					)
			))
			state.vStorage.getValue(testDisk.id).dynamic!!.allocations.single().let { it is VirtualStorageFsAllocation && it.type == VirtualDiskFormat.raw }
		}
	}
}