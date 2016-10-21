package com.github.K0zka.kerub.utils.junix.dmesg

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.join
import com.github.K0zka.kerub.utils.substringBetween
import org.apache.sshd.client.session.ClientSession

object BsdDmesg {
	fun listCpuFlags(session: ClientSession): List<String> {
		return session.executeOrDie("grep Features= /var/run/dmesg.boot").lines().map {
			line ->
			line.substringBetween("<", ">").split(",").toList().map(String::toLowerCase)
		}.join()
	}
}