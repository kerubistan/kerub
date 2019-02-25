package com.github.kerubistan.kerub.model.dynamic

import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class SimpleStorageDeviceDynamicTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException>("invalid freeCapacity") {
			SimpleStorageDeviceDynamic(
					id = UUID.randomUUID(),
					freeCapacity = (-1).toBigInteger()
			)
		}
	}
}