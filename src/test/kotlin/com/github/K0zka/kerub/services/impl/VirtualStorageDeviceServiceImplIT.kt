package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.createServiceClient
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.expectations.StorageReadPerformanceExpectation
import com.github.K0zka.kerub.model.expectations.StorageRedundancyExpectation
import com.github.K0zka.kerub.model.expectations.VirtualStorageExpectation
import com.github.K0zka.kerub.model.io.IoTune
import com.github.K0zka.kerub.services.LoginService
import com.github.K0zka.kerub.services.VirtualStorageDeviceService
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class VirtualStorageDeviceServiceImplIT {
	@Test
	fun crud() {
		val client = createClient()
		setAccountsRequired(false)
		val vsd = createServiceClient(VirtualStorageDeviceService::class, client)
		val loginClient = createServiceClient(LoginService::class, client)
		loginClient.login(LoginService.UsernamePassword(username = "admin", password = "password"))
		val randomUUID = UUID.randomUUID()
		val deviceToSave = VirtualStorageDevice(
				id = randomUUID,
				name = "virtual disk ${randomUUID}",
				size = "100 GB".toSize(),
				readOnly = false,
				expectations = listOf(
						StorageRedundancyExpectation(
								nrOfCopies = 2
						                            ),
						StorageReadPerformanceExpectation(
								speed = IoTune(
										kbPerSec = 1024,
										iopsPerSec = 1024
								              )
						                                 )
				                     )
		                                       )
		vsd.add(deviceToSave)
		val savedDevice = vsd.getById(deviceToSave.id)
		Assert.assertEquals(deviceToSave, savedDevice)

		val update: VirtualStorageDevice = savedDevice.copy(size = deviceToSave.size + "200 MB".toSize())
		vsd.update(update.id, update)

		val updated = vsd.getById(deviceToSave.id)
		Assert.assertEquals(update, updated)

		val search = vsd.search("name", savedDevice.name)
		Assert.assertFalse(search.result.isEmpty())

		vsd.delete(deviceToSave.id)

	}
}