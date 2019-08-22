package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.dynamic.gvinum.GvinumStorageDeviceDynamicItem
import com.github.kerubistan.kerub.model.dynamic.lvm.LvmStorageDeviceDynamicItem
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class CompositeStorageDeviceDynamicItemTest {
	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			LvmStorageDeviceDynamicItem(
					name = "sdb",
					freeCapacity = (-1).toBigInteger()
			)
		}
		assertThrows<IllegalStateException> {
			GvinumStorageDeviceDynamicItem(
					name = "sdb",
					freeCapacity = (-1).toBigInteger()
			)
		}
	}
}