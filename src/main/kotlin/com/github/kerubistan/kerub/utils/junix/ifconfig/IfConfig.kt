package com.github.kerubistan.kerub.utils.junix.ifconfig

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.substringAfterOrNull
import com.github.kerubistan.kerub.utils.substringBetweenOrNull
import io.github.kerubistan.kroki.strings.substringBetween
import org.apache.sshd.client.session.ClientSession
import java.util.regex.Pattern

object IfConfig {
	private val itemRegex = "\n(\\b)".toRegex()

	fun list(session: ClientSession): List<NetInterface> =
			session.executeOrDie("ifconfig").split(itemRegex, Pattern.DOTALL).filter { it.isNotBlank() }.map { str ->
				NetInterface(
						name = str.substringBefore(":"),
						mtu = str.substringBetween("mtu ", "\n").toInt(),
						mac = str.substringAfterOrNull("ether ")?.substring(startIndex = 0, endIndex = 17),
						inet4Addr = str.substringBetweenOrNull("inet ", " "),
						inet6Addr = str.substringBetweenOrNull("inet6 ", " ")
				)
			}
}