package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.testOtherHost
import io.github.kerubistan.kroki.time.now
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class VirtualStorageDataCollectionTest {

	@Test
	fun updateDynamic() {
		VirtualStorageDataCollection(
				stat = testDisk,
				dynamic = VirtualStorageDeviceDynamic(
						id = testDisk.id,
						allocations = listOf()
				)
		).updateDynamic {
			it.copy(lastUpdated = now())
		}
	}

	@Test
	fun updateWithDynamic() {
		val original = VirtualStorageDataCollection(
				stat = testDisk,
				dynamic = VirtualStorageDeviceDynamic(
						id = testDisk.id,
						allocations = listOf()
				)
		)

		val updated = original.updateWithDynamic {
			it.copy(
					allocations = listOf(
							VirtualStorageLvmAllocation(
									hostId = testHost.id,
									capabilityId = testLvmCapability.id,
									actualSize = testDisk.size,
									path = "",
									vgName = testLvmCapability.volumeGroupName
							)
					))
		}


		assertEquals(original.stat, updated.stat)
		assertNotEquals(original.dynamic, updated.dynamic)
	}


	@Test
	fun validation() {
		assertThrows<IllegalStateException>("stat and dyn id must be the same") {
			VirtualStorageDataCollection(
					stat = testDisk,
					dynamic = VirtualStorageDeviceDynamic(
							id = UUID.randomUUID(),
							allocations = listOf()
					)
			)
		}
		assertThrows<IllegalStateException>("rw disk with multiple allocations") {
			VirtualStorageDataCollection(
					stat = testDisk,
					dynamic = VirtualStorageDeviceDynamic(
							id = testDisk.id,
							allocations = listOf(
									VirtualStorageLvmAllocation(
											capabilityId = testLvmCapability.id,
											actualSize = testDisk.size,
											vgName = testLvmCapability.volumeGroupName,
											path = "",
											hostId = testHost.id
									),
									VirtualStorageLvmAllocation(
											capabilityId = testLvmCapability.id,
											actualSize = testDisk.size,
											vgName = testLvmCapability.volumeGroupName,
											path = "",
											hostId = testOtherHost.id
									)
							)
					)
			)
		}
	}
}