package com.github.kerubistan.kerub.model.alerts

import com.github.kerubistan.kerub.model.AbstractDataRepresentationTest
import com.github.kerubistan.kerub.testHost
import io.github.kerubistan.kroki.time.now
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

internal class StorageFailureAlertTest : AbstractDataRepresentationTest<StorageFailureAlert>() {
	override val testInstances: Collection<StorageFailureAlert>
		get() = listOf(
				StorageFailureAlert(
						id = randomUUID(),
						open = true,
						created = now(),
						resolved = null,
						hostId = testHost.id,
						deviceId = "/dev/sda",
						storageDevice = "/dev/sda",
						hotSwap = null
				)
		)
	override val clazz = StorageFailureAlert::class.java

	@Test
	fun validate() {
		assertThrows<IllegalStateException>("Open but resolved...") {
			StorageFailureAlert(
					id = randomUUID(),
					open = true,
					created = now(),
					resolved = now(),
					hostId = testHost.id,
					deviceId = "/dev/sda",
					storageDevice = "/dev/sda",
					hotSwap = null
			)
		}
		assertThrows<IllegalStateException>("closed without resolution time") {
			StorageFailureAlert(
					id = randomUUID(),
					open = false,
					created = now(),
					resolved = null,
					hostId = testHost.id,
					deviceId = "/dev/sda",
					storageDevice = "/dev/sda",
					hotSwap = null
			)
		}
		assertThrows<IllegalStateException>("first resolved, then created...") {
			StorageFailureAlert(
					id = randomUUID(),
					open = false,
					created = now() + 1000,
					resolved = now() - 1000,
					hostId = testHost.id,
					deviceId = "/dev/sda",
					storageDevice = "/dev/sda",
					hotSwap = null
			)
		}
	}
}