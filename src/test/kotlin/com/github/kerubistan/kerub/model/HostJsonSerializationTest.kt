package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.createObjectMapper
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class HostJsonSerializationTest {
	@Test
	fun serializeDeserialize() {
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						blockDevices = listOf(
								BlockDevice(deviceName = "sda", storageCapacity = 2.TB),
								BlockDevice(deviceName = "sdb", storageCapacity = 2.TB),
								BlockDevice(deviceName = "sdc", storageCapacity = 2.TB)
						),
						storageCapabilities = listOf(
								LvmStorageCapability(
										physicalVolumes = mapOf(
												"/dev/sda" to 2.TB,
												"/dev/sdb" to 2.TB,
												"/dev/sdc" to 2.TB),
										size = 6.TB,
										volumeGroupName = "test-vg",
										id = UUID.randomUUID()
								)
						)
				)
		)
		val objectMapper = createObjectMapper(prettyPrint = true)
		val serializedHost = objectMapper.writeValueAsString(host)
		val deserializedHost = objectMapper.readValue(serializedHost, Host::class.java)
		assertEquals(host, deserializedHost)
	}
}