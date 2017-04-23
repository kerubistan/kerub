package com.github.K0zka.kerub.model.controller.config

import com.github.K0zka.kerub.model.FsStorageCapability
import com.github.K0zka.kerub.utils.toSize
import org.junit.Test
import kotlin.test.assertEquals

class StorageTechnologiesConfigTest {
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