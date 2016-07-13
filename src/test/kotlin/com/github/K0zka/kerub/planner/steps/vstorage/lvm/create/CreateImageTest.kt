package com.github.K0zka.kerub.planner.steps.vstorage.lvm.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.utils.toSize
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

		Assert.assertTrue(state.vStorageDyns.values.any {
			it.allocation.hostId == host.id
					&& it.id == device.id
		})
	}
}