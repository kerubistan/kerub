package com.github.K0zka.kerub.utils.junix.ethtool

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object EthTool {
	fun getDeviceInformation(session: ClientSession, deviceName: String): EthernetDeviceInformation =
			parse(session.execute("ethtool $deviceName"))

	internal fun parse(output: String): EthernetDeviceInformation {
		val props = output.lines()
		return EthernetDeviceInformation(
				wakeOnLan = props.any { it.trim() == "Wake-on: g" },
				link = props.any { it.trim() == "Link detected: yes" },
				transferRate = props.firstOrNull {
					it.trim().startsWith("Speed:")
				}?.substringAfter(":")?.substringBefore("/s")?.toSize() ?: BigInteger.ZERO
		)
	}
}
