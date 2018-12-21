package com.github.kerubistan.kerub.utils.junix.lsblk

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.flag
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.skip
import com.github.kerubistan.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession

object Lsblk : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?) = hostCapabilities?.os == OperatingSystem.Linux

	private val spaces = "\\s+".toRegex()
	private const val TRUE = "1"

	fun list(session: ClientSession, noDeps: Boolean = false): List<BlockDeviceInfo> {
		return session.executeOrDie("lsblk -l -o NAME,ROTA,RO,RA,RM,MIN-IO,OPT-IO,TYPE,SIZE ${noDeps.flag("-d")}")
				.lines().let {
					val header = it.first().split(spaces).map { it.trim() }
					val data = it.skip().filter { it.isNotBlank() }

					data.map {
						val columns = it.split(spaces)
						BlockDeviceInfo(
								name = columns[header.indexOf("NAME")],
								type = columns[header.indexOf("TYPE")],
								size = columns[header.indexOf("SIZE")].toSize(),
								minIo = columns[header.indexOf("MIN-IO")].toBigInteger(),
								optIo = columns[header.indexOf("OPT-IO")].toBigInteger(),
								removable = columns[header.indexOf("RM")] == TRUE,
								rotational = columns[header.indexOf("ROTA")] == TRUE,
								readOnly = columns[header.indexOf("RO")] == TRUE,
								readAhead = columns[header.indexOf("RA")].toBigInteger()
						)
					}
				}
	}
}