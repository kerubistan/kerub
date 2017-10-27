package com.github.kerubistan.kerub.utils.junix.mpstat

import com.github.kerubistan.kerub.model.dynamic.CpuStat
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

object MPStat : OsCommand {

	private val regex = "\\s+".toRegex()

	class MPStatOutput(private val handler: (List<CpuStat>) -> Unit) : OutputStream() {
		private val buff: StringBuilder = StringBuilder(128)
		var cpuStats = listOf<CpuStat>()
		override fun write(data: Int) {
			if (data == 10) {
				parseOutput()
			} else {
				buff.append(data.toChar())
			}
		}

		private fun parseOutput() {
			val line = buff.toString()
			buff.setLength(0)

			if (line.isBlank()) {
				handler(
						cpuStats.sortedBy { it.cpuNr }
				)
				cpuStats = listOf()
			} else {
				val fields = line.split(regex)
				if (fields.size == 13 && fields[2] != "all" && fields[2] != "CPU") {
					cpuStats += CpuStat(
							cpuNr = fields[2].toInt(),
							idle = fields[12].toFloat(),
							ioWait = fields[6].toFloat(),
							system = fields[5].toFloat(),
							user = fields[3].toFloat()
					)
				}
			}
		}

	}

	fun monitor(session: ClientSession, handler: (List<CpuStat>) -> Unit, interval: Short = 1) {
		val channel = session.createExecChannel("mpstat ${interval} -P ALL")
		channel.`in` = NullInputStream(0)
		channel.err = NullOutputStream()
		channel.out = MPStatOutput(handler)
		channel.open().verify()
	}
}