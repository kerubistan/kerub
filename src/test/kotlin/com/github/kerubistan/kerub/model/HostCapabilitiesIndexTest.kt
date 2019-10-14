package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.testHostCapabilities
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HostCapabilitiesIndexTest {

	@Test
	fun lvmStorageCapabilitiesByVolumeGroupName() {
		assertTrue {
			val lvmCap = LvmStorageCapability(
					id = randomUUID(),
					size = 1.TB,
					physicalVolumes = mapOf("/dev/sda" to 1.GB),
					volumeGroupName = "vg-1"
			)
			testHostCapabilities.copy(
					storageCapabilities = listOf(lvmCap),
					blockDevices = listOf(
							BlockDevice("/dev/sda", 1.TB)
					)
			).index.lvmStorageCapabilitiesByVolumeGroupName.let {
				it.size == 1
						&& it.keys.contains("vg-1")
						&& it.values.contains(lvmCap)
			}
		}
	}

	@Test
	fun getCompressionCapabilities() {
		assertTrue("no compression packages, no compression capabilities") {
			testHostCapabilities.copy(
				installedSoftware = listOf(
				)
			).index.compressionCapabilities.isEmpty()
		}
		assertTrue("gzip installed") {
			testHostCapabilities.copy(
					installedSoftware = listOf(
							pack("gzip", "1.2.3")
					)
			).index.compressionCapabilities == setOf(CompressionFormat.Gzip)
		}
	}

	@Test
	fun installedPackageNames() {
		assertEquals(
				setOf("gzip"),
				testHostCapabilities.copy(
						installedSoftware = listOf(
								pack("gzip", "1.2.3")
						)
				).index.installedPackageNames
		)
	}
}