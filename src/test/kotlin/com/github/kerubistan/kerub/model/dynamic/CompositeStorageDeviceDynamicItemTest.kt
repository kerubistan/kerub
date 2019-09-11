package com.github.kerubistan.kerub.model.dynamic

import org.junit.Test
import org.junit.jupiter.api.assertThrows

class CompositeStorageDeviceDynamicItemTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			CompositeStorageDeviceDynamicItem(
					name = "sdb",
					freeCapacity = (-1).toBigInteger()
			)
		}
		assertThrows<IllegalStateException> {
			CompositeStorageDeviceDynamicItem(
					name = "sdb",
					freeCapacity = (-1).toBigInteger()
			)
		}
	}
}