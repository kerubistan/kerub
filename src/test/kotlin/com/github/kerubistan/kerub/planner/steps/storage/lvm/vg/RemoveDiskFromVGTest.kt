package com.github.kerubistan.kerub.planner.steps.storage.lvm.vg

import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class RemoveDiskFromVGTest {

	@Test
	fun validations() {
		assertThrows<IllegalArgumentException> {
			RemoveDiskFromVG(
					host = testHost,
					capability = testLvmCapability,
					device = testLvmCapability.physicalVolumes.keys.first())
		}
		assertThrows<IllegalStateException>("lvm capability not registered - must die") {
			RemoveDiskFromVG(
					host = testHost.copy(
							capabilities = testHostCapabilities
					),
					capability = testLvmCapability,
					device = testLvmCapability.physicalVolumes.keys.first())
		}

		assertThrows<IllegalStateException>("lvm pv not registered - must die") {
			RemoveDiskFromVG(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(testLvmCapability)
							)
					),
					capability = testLvmCapability,
					device = testLvmCapability.physicalVolumes.keys.first() + "_SOME_OTHER_DEVICE")
		}

		assertThrows<IllegalStateException>("lvm pv not registered - must die") {
			RemoveDiskFromVG(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(testLvmCapability)
							)
					),
					capability = testLvmCapability,
					device = testLvmCapability.physicalVolumes.keys.first() + "_SOME_OTHER_DEVICE")
		}

	}

	@Test
	fun take() {
		val lvmStorageCapability = testLvmCapability.copy(
				physicalVolumes = testLvmCapability.physicalVolumes
						+ ("/dev/sdb" to 2.TB)
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(lvmStorageCapability)
				)
		)
		val hostDynamic = hostUp(host).copy(

		)
		val state = RemoveDiskFromVG(
				host = host,
				capability = lvmStorageCapability,
				device = lvmStorageCapability.physicalVolumes.keys.first()).take(
				OperationalState.fromLists(
						hosts = listOf(host),
						hostDyns = listOf(hostDynamic)
				)
		)
		assertTrue {
			state.hosts.getValue(host.id).stat.capabilities!!.storageCapabilitiesById.getValue(testLvmCapability.id)
					.let { cap ->
				cap is LvmStorageCapability &&
						cap.size < lvmStorageCapability.size
						&& cap.physicalVolumes.keys.size == lvmStorageCapability.physicalVolumes.keys.size - 1
			}
		}
	}

	@Test
	fun reservations() {
		assertTrue("The host should be reserved for use") {
			val lvmStorageCapability = testLvmCapability.copy(
					physicalVolumes = testLvmCapability.physicalVolumes
							+ ("/dev/sdb" to 2.TB)
			)
			RemoveDiskFromVG(
					host = testHost.copy(
							capabilities = testHostCapabilities.copy(
									storageCapabilities = listOf(
											lvmStorageCapability
									)
							)
					),
					capability = lvmStorageCapability,
					device = lvmStorageCapability.physicalVolumes.keys.first())
					.reservations()
					.isNotEmpty()
		}
	}
}