package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFsCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class CreateImageTest : OperationalStepVerifications() {
	val device = VirtualStorageDevice(
			size = 128.GB,
			readOnly = false,
			name = "test"
	)

	val host = Host(
			address = "host-1.example.com",
			publicKey = "",
			dedicated = true,
			capabilities = testHostCapabilities.copy(
					storageCapabilities = listOf(testFsCapability)
			)
	)

	override val step= CreateImage(device, testFsCapability, host, VirtualDiskFormat.qcow2)

	@Test
	fun take() {
		val state = CreateImage(device, testFsCapability, host, VirtualDiskFormat.qcow2).take(OperationalState.fromLists(
				hosts = listOf(host),
				vStorage = listOf(device)
		))

		assertTrue(state.vStorage.values.any {
			it.dynamic?.allocations?.single()?.hostId == host.id
					&& it.dynamic?.id == device.id
		})
	}

	@Test
	fun validations() {
		assertThrows<IllegalArgumentException> {
			CreateImage(
					host = testHost,
					capability = testFsCapability,
					disk = testDisk,
					format = VirtualDiskFormat.qcow2
			)
		}
	}
}