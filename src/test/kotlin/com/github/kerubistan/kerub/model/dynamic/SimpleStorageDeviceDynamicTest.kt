package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

class SimpleStorageDeviceDynamicTest : AbstractDataRepresentationTest<SimpleStorageDeviceDynamic>(){
	override val testInstances = listOf(
			SimpleStorageDeviceDynamic(
					id = randomUUID(),
					freeCapacity = 4.TB
			)
	)
	override val clazz = SimpleStorageDeviceDynamic::class.java

	@Test
	fun validations() {
		assertThrows<IllegalStateException>("invalid freeCapacity") {
			SimpleStorageDeviceDynamic(
					id = randomUUID(),
					freeCapacity = (-1).toBigInteger()
			)
		}
	}
}