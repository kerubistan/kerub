package com.github.kerubistan.kerub.utils

import com.github.kerubistan.kerub.utils.junix.sysfs.Net
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
		var bytes = ByteArray(6)
		bytes[0] = 0xFF.toByte()
		bytes[1] = 0xFF.toByte()
		bytes[2] = 0xFF.toByte()
		bytes[3] = 0xFF.toByte()
		bytes[4] = 0xFF.toByte()
		bytes[5] = 0xFF.toByte()
		assertEquals(macToString(bytes), "FF:FF:FF:FF:FF:FF")
		bytes = ByteArray(6)
		bytes[0] = 0x0A.toByte()
		bytes[1] = 0xA0.toByte()
		bytes[2] = 0x01.toByte()
		bytes[3] = 0x10.toByte()
		bytes[4] = 0x40.toByte()
		bytes[5] = 0x04.toByte()
		assertEquals(macToString(bytes), "0A:A0:01:10:40:04")
	}

	@Test(expected = IllegalArgumentException::class)
	fun macToStringWithWrongLength() {
		macToString(ByteArray(7))
	}

}