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

fun macToString(mac: ByteArray): String {
	require(mac.size == Net.macAddressSize, { "The MAC address must be 6 bytes" })
	return buildString(17) {
		for (b in mac) {
			if (length != 0) {
				append(':')
			}
			val hexString = Integer.toHexString(b.toInt())
			if (hexString.length == 1) {
				append('0')
			}
			append(hexString)
		}
	}
}
