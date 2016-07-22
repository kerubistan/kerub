package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.utils.junix.sysfs.Net
import org.junit.Assert.assertEquals
import org.junit.Test

class EthernetUtilsKtTest {

	@Test
	fun stringToMac() {
		val mac = stringToMac("52:54:00:b5:75:bb")
		assertEquals(mac.size, Net.macAddressSize)
	}

	@Test(expected = IllegalArgumentException::class)
	fun stringToMacWithWrongLength() {
		val mac = stringToMac("52:54:00:b5:75:bb:ff")
	}

	@Test(expected = IllegalArgumentException::class)
	fun stringToMacWithWrongByteLength() {
		val mac = stringToMac("52:54:00:b5:75:bbbb:ff")
	}

	@Test
	fun macToString() {
		assertEquals(macToString(ByteArray(6)), "00:00:00:00:00:00")
	}

	@Test(expected = IllegalArgumentException::class)
	fun macToStringWithWrongLength() {
		macToString(ByteArray(7))
	}

}