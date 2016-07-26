package com.github.K0zka.kerub.utils.junix.procfs

import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

object Stat {

	private val separator = "--separator"
	private val fieldSeparator = "\\s+".toRegex()

	class CpuLoadMonitrOutputStream(val handler: (Map<String, CpuStat>) -> Unit) : OutputStream() {
		val buffer = StringBuilder()
		override fun write(input: Int) {
			buffer.append(input.toChar())
			if (buffer.endsWith(separator)) {
				handler(
						buffer.removeSuffix(separator).toString().trim().lines().filterNot { it == separator }.map {
							line ->
							val fields = line.split(fieldSeparator)
							require(fields.size > 4) { "Something is missing from here: $line" }
							fields[0] to
									CpuStat(
											//based on procfs manual page:
											// user time + user nice
											user = fields[1].toInt() + fields[2].toInt(),
											system = fields[3].toInt(),
											idle = fields[4].toInt()
									)
						}.toMap()
				)
			}
		}
	}

	fun cpuLoadMonitor(session: ClientSession, handler: (Map<String, CpuStat>) -> Unit) {
		val channel = session.createExecChannel(
				"""bash -c "while true; do grep cpu /proc/stat; echo $separator; sleep 1; done;" """
		)
		channel.`in` = NullInputStream(0)
		channel.err = NullOutputStream()
		channel.out = CpuLoadMonitrOutputStream(handler)
		channel.open().verify()
	}

	fun cpuLoadMonitorIncremental(session: ClientSession, handler: (Map<String, CpuStat>) -> Unit) {
		var previous: Map<String, CpuStat>? = null
		cpuLoadMonitor(session) {
			newStats ->
			if (previous != null) {
				handler(previous!!.map {
					val prev = requireNotNull(previous!![it.key])
					val curr = it.value
					it.key to curr.copy(
							user = curr.user - prev.user,
							system = curr.system - prev.system,
							idle = curr.idle - prev.idle
					)
				}.toMap()
				)
			} else {
				previous = newStats
			}
		}
	}
}
