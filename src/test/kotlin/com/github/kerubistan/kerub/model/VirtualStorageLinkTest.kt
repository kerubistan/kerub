package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.testDisk
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class VirtualStorageLinkTest {

	@Test
	fun validations() {
		assertThrows<IllegalStateException> {
			VirtualStorageLink(
					device = DeviceType.cdrom,
					bus = BusType.virtio,
					readOnly = true,
					virtualStorageId = testDisk.id
			)
		}
	}
}