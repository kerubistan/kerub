package com.github.kerubistan.kerub.planner.steps.vstorage.fs.truncate

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class TruncateImageTest {

	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			TruncateImage(
					host = testHost,
					allocation = VirtualStorageFsAllocation(
							hostId = testHost.id,
							type = VirtualDiskFormat.qcow2, //intentionally wrong
							mountPoint = "/kerub",
							fileName = "XXX.qcow2",
							actualSize = 100.GB),
					disk = testDisk
			)
		}
		assertThrows<IllegalStateException> {
			TruncateImage(
					host = testHost,
					allocation = VirtualStorageFsAllocation(
							hostId = testOtherHost.id, //intentionally wrong
							type = VirtualDiskFormat.raw,
							mountPoint = "/kerub",
							fileName = "XXX.qcow2",
							actualSize = 100.GB),
					disk = testDisk
			)
		}
	}

	@Test
	fun take() {
		val fsAllocation = VirtualStorageFsAllocation(
				hostId = testHost.id,
				type = VirtualDiskFormat.raw,
				mountPoint = "/kerub",
				fileName = "XXX.qcow2",
				actualSize = 100.GB)
		val state = TruncateImage(
				host = testHost,
				allocation = fsAllocation,
				disk = testDisk
		).take(
				OperationalState.fromLists(
						vStorage = listOf(testDisk)
				)
		)

		assertEquals( state.vStorage[testDisk.id]?.dynamic?.allocations, listOf(fsAllocation))
	}
}