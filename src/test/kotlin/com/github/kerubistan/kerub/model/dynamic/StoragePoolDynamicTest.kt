package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows

internal class StoragePoolDynamicTest : AbstractDataRepresentationTest<StoragePoolDynamic>() {
	override val testInstances = listOf(
			StoragePoolDynamic(
					name = "TEST",
					size = 1.TB,
					freeSize = 512.GB
			)
	)
	override val clazz = StoragePoolDynamic::class.java

	@Test
	fun validation() {
		assertThrows<IllegalStateException> {
			StoragePoolDynamic(name = "test", size = (-1).toBigInteger(), freeSize = 1.TB)
		}
		assertThrows<IllegalStateException> {
			StoragePoolDynamic(name = "test", size = 1.TB, freeSize = (-1).TB)
		}
		assertThrows<IllegalStateException> {
			StoragePoolDynamic(name = "test", size = 1.TB, freeSize = 2.TB)
		}
	}
}