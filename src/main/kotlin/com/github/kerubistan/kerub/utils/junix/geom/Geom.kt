package com.github.kerubistan.kerub.utils.junix.geom

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object Geom : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?) = hostCapabilities?.os == OperatingSystem.BSD

	fun list(session: ClientSession): List<GeomStorageDevice> =
			session.executeOrDie("geom disk list -a").split("\n\n").filter(String::isNotBlank).map {
				val props = it.lines().associate { it.substringBefore(":").trim() to it.substringAfter(":").trim() }
				GeomStorageDevice(
						name = props["Geom name"] ?: "",
						rotationRate = null,
						mediaSize = props["Mediasize"]?.substringBefore(" ")?.toBigInteger() ?: BigInteger.ZERO
				)
			}

}