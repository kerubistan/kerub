package com.github.K0zka.kerub.utils.junix.ifconfig

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.substringBetween
import org.apache.sshd.client.session.ClientSession
import java.util.regex.Pattern

object IfConfig {
	private val itemRegex = "\n(\\b)".toRegex()

	fun list(session: ClientSession): List<NetInterface> =
			session.executeOrDie("ifconfig").split(itemRegex, Pattern.DOTALL).filter { it.isNotBlank() }.map {
				str ->
				NetInterface(
						name = str.substringBefore(":"),
						mtu = str.substringBetween("mtu ", "\n").toInt(),
						mac = str.substringBetween("ether ", "\n"),
						inet4Addr = str.substringBetween("inet ", " "),
						inet6Addr = str.substringBetween("inet6 ", " ")
				)
			}
}