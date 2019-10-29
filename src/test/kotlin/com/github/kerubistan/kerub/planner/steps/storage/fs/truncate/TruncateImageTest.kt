package com.github.kerubistan.kerub.planner.steps.storage.fs.truncate

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class TruncateImageTest : OperationalStepVerifications() {
	override val step = TruncateImage(
			host = testHost,
			allocation = VirtualStorageFsAllocation(
					hostId = testHost.id,
					type = VirtualDiskFormat.raw,
					mountPoint = "/kerub",
					fileName = "/kerub/XXX.raw",
					actualSize = 100.GB,
					capabilityId = testFsCapability.id
			),
			disk = testDisk,
			capability = testFsCapability
	)

	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			TruncateImage(
					host = testHost,
					allocation = VirtualStorageFsAllocation(
							hostId = testHost.id,
							type = VirtualDiskFormat.qcow2, //intentionally wrong
							mountPoint = "/kerub",
							fileName = "/kerub/XXX.qcow2",
							actualSize = 100.GB,
							capabilityId = testFsCapability.id),
					disk = testDisk,
					capability = testFsCapability
			)
		}
		assertThrows<IllegalStateException> {
			TruncateImage(
					host = testHost,
					allocation = VirtualStorageFsAllocation(
							hostId = testOtherHost.id, //intentionally wrong
							type = VirtualDiskFormat.raw,
							mountPoint = "/kerub",
							fileName = "/kerub/XXX.raw",
							actualSize = 100.GB,
							capabilityId = testFsCapability.id),
					disk = testDisk,
					capability = testFsCapability
			)
		}
	}

	@Test
	fun take() {
		val fsAllocation = VirtualStorageFsAllocation(
				hostId = testHost.id,
				type = VirtualDiskFormat.raw,
				mountPoint = "/kerub",
				fileName = "/kerub/XXX.raw",
				actualSize = 100.GB,
				capabilityId = testFsCapability.id
		)
		val state = TruncateImage(
				host = testHost,
				allocation = fsAllocation,
				disk = testDisk,
				capability = testFsCapability
		).take(
				OperationalState.fromLists(
						vStorage = listOf(testDisk)
				)
		)

		assertEquals(state.vStorage[testDisk.id]?.dynamic?.allocations, listOf(fsAllocation))
	}
}