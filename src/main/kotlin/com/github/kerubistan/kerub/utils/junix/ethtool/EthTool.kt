package com.github.kerubistan.kerub.utils.junix.ethtool

import com.github.kerubistan.kerub.host.execute
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.equalsAnyIgnoreCase
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object EthTool : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?) =
			hostCapabilities?.installedSoftware?.any { it.name.equalsAnyIgnoreCase("ethtool") } ?: false

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
