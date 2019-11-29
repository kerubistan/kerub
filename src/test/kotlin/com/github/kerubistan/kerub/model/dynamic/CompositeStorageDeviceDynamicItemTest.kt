package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class CompositeStorageDeviceDynamicItemTest : AbstractDataRepresentationTest<CompositeStorageDeviceDynamicItem>() {
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

	override val testInstances: Collection<CompositeStorageDeviceDynamicItem>
		get() = listOf(
				CompositeStorageDeviceDynamicItem(
						name = "",
						freeCapacity = 100.GB
				)
		)
	override val clazz: Class<CompositeStorageDeviceDynamicItem>
		get() = CompositeStorageDeviceDynamicItem::class.java
}