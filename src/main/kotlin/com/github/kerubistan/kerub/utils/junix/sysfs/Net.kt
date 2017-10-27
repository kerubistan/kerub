package com.github.kerubistan.kerub.utils.junix.sysfs

import com.github.kerubistan.kerub.host.getFileContents
import com.github.kerubistan.kerub.utils.stringToMac
import org.apache.sshd.client.session.ClientSession

/**
 * Utilities to deal with the linux sysfs network information.
 */
object Net {

	val macAddressSize = 6
	val hexaDecimal = 16

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
				stringToMac(it.getFileContents("/sys/class/net/$device/address"))
			}
}