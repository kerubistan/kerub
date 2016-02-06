package com.github.K0zka.kerub.host.lom

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.utils.junix.sysfs.Net
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class) class WakeOnLanTest {

	@Mock
	var executor : HostCommandExecutor? = null
	@Mock
	var hostManager : HostManager? = null
	val host = Host(
			address = "host-1.example.com",
	        dedicated = true,
	        publicKey = "",
	        capabilities = HostCapabilities(
			        macAddresses = listOf(Net.stringToMac("00:00:CA:FE:BA:BE")),
	                cpuArchitecture = "x86_64",
	                devices = listOf(),
	                totalMemory = "1234 MB".toSize(),
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
		WakeOnLan(host, executor!!, hostManager!!).on()
	}

	@Test(expected = IllegalArgumentException::class)
	fun onWithoutMacAddress() {
		WakeOnLan(host.copy( capabilities = null), executor!!, hostManager!!).on()
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

	@Test
	fun off() {
		WakeOnLan(host, executor!!, hostManager!!).off()
	}
}