package com.github.K0zka.kerub.utils.junix.sysfs

import com.github.K0zka.kerub.host.getFileContents
import com.github.K0zka.kerub.host.use
import org.apache.sshd.ClientSession

/**
 * Utilities to deal with the linux sysfs network information.
 */
public object Net {
	/**
	 * List ethernet devices.
	 */
	fun listDevices(session: ClientSession): List<String> =
			session.createSftpClient().readDir("/sys/class/net/").toList().map { it.filename }

	/**
	 * Get MAC address of a device.
	 */
	fun getMacAddress(session: ClientSession, device: String) =
			session.createSftpClient().use {
				it.getFileContents("/sys/class/net/${device}")
						.split(':')
						.map { Integer.parseInt(it, 16).toByte() }
						.toByteArray()
			}
}