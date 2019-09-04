package com.github.kerubistan.kerub.utils.junix.mpstat

import com.github.kerubistan.kerub.host.process
import com.github.kerubistan.kerub.model.dynamic.CpuStat
import com.github.kerubistan.kerub.utils.junix.common.MonitorOutputStream
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object MPStat : OsCommand {

	private val spaces = "\\s+".toRegex()

	private fun parseRows(rows : String) = rows.lines().filterNot { it.isBlank() }.mapNotNull { line ->
		val fields = line.split(spaces)
		if (fields.size == 13 && fields[2] != "all" && fields[2] != "CPU") {
			CpuStat(
					cpuNr = fields[2].toInt(),
					idle = fields[12].toFloat(),
					ioWait = fields[6].toFloat(),
					system = fields[5].toFloat(),
					user = fields[3].toFloat()
			)
		} else null
	}

	fun monitor(session: ClientSession, handler: (List<CpuStat>) -> Unit, interval: Short = 1) {
		session.process(
				"mpstat $interval -P ALL",
				MonitorOutputStream(separator = "\n\n", callback = handler, parser = this::parseRows)
		)
	}
}