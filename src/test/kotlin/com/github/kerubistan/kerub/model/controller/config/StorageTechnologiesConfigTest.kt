package com.github.kerubistan.kerub.model.controller.config

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class StorageTechnologiesConfigTest {

	@Test
	fun validations() {
		assertThrows<Exception> {
			StorageTechnologiesConfig(
				lvmVGPattern = "\\"
			)
		}
		StorageTechnologiesConfig(
				lvmVGPattern = ".*"
		)
	}

	@Test
	fun enabledCapabilities() {
		val shouldPass = FsStorageCapability(size = "128 GB".toSize(), mountPoint = "/kerub", fsType = "ext4")
		val capabilities = StorageTechnologiesConfig(fsPathEnabled = listOf("/var", "/kerub"), fsTypeEnabled = listOf("ext4", "ext3"))
				.enabledCapabilities(
						listOf(
								FsStorageCapability(size = "128 GB".toSize(), mountPoint = "/home", fsType = "ext4"),
								shouldPass,
								FsStorageCapability(size = "128 GB".toSize(), mountPoint = "/var", fsType = "xfs")
						)
				)
		assertEquals(capabilities,
				listOf(shouldPass))
	}

}