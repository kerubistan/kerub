package com.github.K0zka.kerub.utils.junix.mpstat

import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.ClientSession
import java.io.OutputStream

object MPStat {

	private val regex = "\\s+".toRegex()
	private val timeregex = "\\d\\d:\\d\\d:\\d\\d"

	class MPStatOutput(private val handler: (CpuStat) -> Unit) : OutputStream() {
		private val buff: StringBuilder = StringBuilder(128)
		override fun write(data: Int) {
			if (data == 10) {
				val line = buff.toString()
				buff.setLength(0)

				val fields = line.split(regex)

				if (fields.size == 13 && fields[2] != "all" && fields[2] != "CPU") {
					handler(
							CpuStat(
									cpuNr = fields[2].toInt(),
									idle = fields[12].toFloat(),
									ioWait = fields[6].toFloat(),
									system = fields[5].toFloat(),
									user = fields[3].toFloat()
							)
					)
				}

			} else {
				buff.append(data.toChar())
			}
		}

	}

	fun monitor(session: ClientSession, handler: (CpuStat) -> Unit, interval: Short = 1) {
		val channel = session.createExecChannel("mpstat ${interval} -P ALL")
		channel.`in` = NullInputStream(0)
		channel.err = NullOutputStream()
		channel.out = MPStatOutput(handler)
		channel.open().verify()
	}
}