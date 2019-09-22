package com.github.kerubistan.kerub.model.dynamic

import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows

internal class StoragePoolDynamicTest {
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