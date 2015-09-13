package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.createServiceClient
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.expectations.StorageReadPerformanceExpectation
import com.github.K0zka.kerub.model.expectations.StorageRedundancyExpectation
import com.github.K0zka.kerub.model.io.IoTune
import com.github.K0zka.kerub.services.LoginService
import com.github.K0zka.kerub.services.VirtualStorageDeviceService
import org.junit.Assert
import org.junit.Test
import java.util.UUID

val sizeMultiplier: Long = 1024;

fun Int.KB() = (this * sizeMultiplier)
fun Int.MB() = this.KB() * sizeMultiplier
fun Int.GB() = this.MB() * sizeMultiplier
fun Int.TB() = this.GB() * sizeMultiplier
fun Int.PB() = this.TB() * sizeMultiplier


val sizePostfixes = mapOf<String, (Int) -> Long>(
		"KB" to { l: Int -> l.KB() },
		"MB" to { l: Int -> l.MB() },
		"GB" to { l: Int -> l.GB() },
		"TB" to { l: Int -> l.TB() },
		"PB" to { l: Int -> l.PB() }
                                                )

val numberRegex = "\\d+".toRegex()

fun String.toStorageSize(): Long {
	val unit = this.replace("\\d+".toRegex(), "").trim()
	val num = this.substringBefore(unit).trim().toInt()
	val fn = sizePostfixes.get(unit)
	if(fn == null) {
		throw IllegalArgumentException("Unknown storage unit ${unit} in ${this}")
	} else {
		return fn(num)
	}
}

public class VirtualStorageDeviceServiceImplIT {
	Test
	fun crud() {
		val client = createClient()
		val vsd = createServiceClient(VirtualStorageDeviceService::class, client)
		val loginClient = createServiceClient(LoginService::class, client)
		loginClient.login(LoginService.UsernamePassword(username = "admin", password = "password"))
		val randomUUID = UUID.randomUUID()
		val deviceToSave = VirtualStorageDevice(
				id = randomUUID,
				name = "virtual disk ${randomUUID}",
				size = 100.GB(),
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

		val update: VirtualStorageDevice = savedDevice.copy(size = deviceToSave.size + 200.MB())
		vsd.update(update.id, update)

		val updated = vsd.getById(deviceToSave.id)
		Assert.assertEquals(update, updated)

		vsd.delete(deviceToSave.id)

	}
}