package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.utils.junix.sysfs.Net

fun stringToMac(strMac: String): ByteArray {
	val bytes = strMac.trim().split(':')
	require(bytes.size == Net.macAddressSize, { "The MAC address must be 6 bytes" })
	return bytes
			.map {
				require(it.length <= 2, { "Maximum two hexadecimal digits needed" })
				Integer.parseInt(it, Net.hexaDecimal).toByte()
			}
			.toByteArray()
}

val hexaDigits = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

fun macToString(mac: ByteArray): String {
	require(mac.size == Net.macAddressSize, { "The MAC address must be 6 bytes" })
	return buildString(17) {
		for (b in mac) {
			if (length != 0) {
				append(':')
			}
			java.lang.Byte.toUnsignedInt(b)
			append(hexaDigits[java.lang.Byte.toUnsignedInt(b) and 0xF0 ushr 4])
			append(hexaDigits[java.lang.Byte.toUnsignedInt(b) and 0x0F])
		}
	}
}
