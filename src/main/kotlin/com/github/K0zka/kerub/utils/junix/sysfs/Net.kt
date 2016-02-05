package com.github.K0zka.kerub.utils.junix.sysfs

import com.github.K0zka.kerub.host.getFileContents
import com.github.K0zka.kerub.host.use
import org.apache.sshd.ClientSession

/**
 * Utilities to deal with the linux sysfs network information.
 */
public object Net {

	val macAddressSize = 6
	val hexaDecimal = 16

	fun stringToMac(strMac: String): ByteArray {
		val bytes = strMac.trim().split(':')
		require(bytes.size == macAddressSize, { "The MAC address must be 6 bytes" })
		return bytes
				.map {
					require(it.length <= 2, { "Maximum two hexadecimal digits needed" })
					Integer.parseInt(it, hexaDecimal).toByte()
				}
				.toByteArray()
	}

	fun macToString(mac: ByteArray): String {
		require(mac.size == macAddressSize, { "The MAC address must be 6 bytes" })
		val bldr = StringBuilder(17)

		for (b in mac) {
			if (bldr.length != 0) {
				bldr.append(':')
			}
			val hexString = Integer.toHexString(b.toInt())
			if (hexString.length == 1) {
				bldr.append('0')
			}
			bldr.append(hexString)
		}

		return bldr.toString()
	}

	/**
	 * List ethernet devices.
	 */
	fun listDevices(session: ClientSession): List<String> =
			session.createSftpClient().readDir("/sys/class/net/").toList()
					.filter { it.filename != "." && it.filename != ".." }
					.map { it.filename }

	/**
	 * Get MAC address of a device.
	 */
	fun getMacAddress(session: ClientSession, device: String): ByteArray =
			session.createSftpClient().use {
				stringToMac(it.getFileContents("/sys/class/net/${device}/address"))
			}
}