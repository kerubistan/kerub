package com.github.kerubistan.kerub.planner.steps.vstorage.fs.create

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Assert
import org.junit.Test

class CreateImageTest {

	val device = VirtualStorageDevice(
			size = "128 GB".toSize(),
			readOnly = false,
			name = "test"
	)

	val host = Host(
			address = "host-1.example.com",
			publicKey = "",
			dedicated = true
	)

	@Test
	fun take() {
		val state = CreateImage(device, host, "/var", VirtualDiskFormat.qcow2).take(OperationalState.fromLists(
				hosts = listOf(host),
				vStorage = listOf(device)
		))

		Assert.assertTrue(state.vStorage.values.any {
			it.dynamic?.allocation?.hostId == host.id
					&& it.dynamic?.id == device.id
		})
	}
}