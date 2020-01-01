package com.github.kerubistan.kerub.utils.junix.lsblk

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.flag
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.toSize
import io.github.kerubistan.kroki.collections.skip
import org.apache.sshd.client.session.ClientSession

object Lsblk : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?) = hostCapabilities?.os == OperatingSystem.Linux

	private val spaces = "\\s+".toRegex()
	private const val TRUE = "1"

	fun list(session: ClientSession, noDeps: Boolean = false): List<BlockDeviceInfo> {
		return session.executeOrDie("lsblk -l -o NAME,ROTA,RO,RA,RM,MIN-IO,OPT-IO,TYPE,SIZE ${noDeps.flag("-d")}")
				.lines().let { lines ->
					val header = lines.first().split(spaces).map { column -> column.trim() }
					val data = lines.skip().filter { row -> row.isNotBlank() }

					val nameIdx = header.indexOf("NAME")
					val typeIdx = header.indexOf("TYPE")
					val sizeIdx = header.indexOf("SIZE")
					val minIoIdx = header.indexOf("MIN-IO")
					val optIoIdx = header.indexOf("OPT-IO")
					val rmIdx = header.indexOf("RM")
					val rotaIdx = header.indexOf("ROTA")
					val roIdx = header.indexOf("RO")
					val raIdx = header.indexOf("RA")
					data.map { row ->
						val columns = row.split(spaces)
						BlockDeviceInfo(
								name = columns[nameIdx],
								type = columns[typeIdx],
								size = columns[sizeIdx].toSize(),
								minIo = columns[minIoIdx].toBigInteger(),
								optIo = columns[optIoIdx].toBigInteger(),
								removable = columns[rmIdx] == TRUE,
								rotational = columns[rotaIdx] == TRUE,
								readOnly = columns[roIdx] == TRUE,
								readAhead = columns[raIdx].toBigInteger()
						)
					}
				}
	}
}