package com.github.kerubistan.kerub.host.lom

import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.utils.stringToMac
import com.nhaarman.mockito_kotlin.mock
import io.github.kerubistan.kroki.size.MB
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class WakeOnLanTest {

	val hostManager: HostManager = mock()
	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = "",
			capabilities = HostCapabilities(
					powerManagment = listOf(WakeOnLanInfo(macAddresses = listOf(stringToMac("00:00:CA:FE:BA:BE")))),
					cpuArchitecture = "x86_64",
					devices = listOf(),
					totalMemory = 1234.MB,
					cpus = listOf(),
					installedSoftware = listOf(),
					chassis = null,
					os = null,
					distribution = null
			)
	)

	@Ignore
	@Test
	fun on() {
		WakeOnLan(host).on()
	}

	@Test(expected = IllegalArgumentException::class)
	fun onWithoutMacAddress() {
		WakeOnLan(host.copy(capabilities = null)).on()
	}

	@Test
	fun buildMagicPocket() {
		val mac = ByteArray(6)
		mac[0] = 0
		mac[1] = 1
		mac[2] = 2
		mac[3] = 3
		mac[4] = 4
		mac[5] = 5

		val magicPocket = WakeOnLan.buildMagicPocket(mac)

		Assert.assertEquals(magicPocket.size, 102)
	}

}