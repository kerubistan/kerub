package com.github.kerubistan.kerub.model.controller.config

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.model.FsStorageCapability
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class StorageTechnologiesConfigTest : AbstractDataRepresentationTest<StorageTechnologiesConfig>() {

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
		val shouldPass = FsStorageCapability(size = 128.GB, mountPoint = "/kerub", fsType = "ext4")
		val capabilities = StorageTechnologiesConfig(fsPathEnabled = listOf("/var", "/kerub"), fsTypeEnabled = listOf("ext4", "ext3"))
				.enabledCapabilities(
						listOf(
								FsStorageCapability(size = 128.GB, mountPoint = "/home", fsType = "ext4"),
								shouldPass,
								FsStorageCapability(size = 128.GB, mountPoint = "/var", fsType = "xfs")
						)
				)
		assertEquals(capabilities,
				listOf(shouldPass))
	}

	override val testInstances: Collection<StorageTechnologiesConfig>
		get() = listOf(StorageTechnologiesConfig())
	override val clazz: Class<StorageTechnologiesConfig>
		get() = StorageTechnologiesConfig::class.java

}