package com.github.kerubistan.kerub.utils.junix.dmesg

import com.github.kerubistan.kerub.host.executeOrDie
import io.github.kerubistan.kroki.collections.concat
import io.github.kerubistan.kroki.strings.substringBetween
import org.apache.sshd.client.session.ClientSession

object BsdDmesg {
	fun listCpuFlags(session: ClientSession): List<String> {
		return session.executeOrDie("grep Features /var/run/dmesg.boot").lines().map { line ->
			line.substringBetween("<", ">").split(",").toList().map(String::toLowerCase)
		}.concat()
	}
}