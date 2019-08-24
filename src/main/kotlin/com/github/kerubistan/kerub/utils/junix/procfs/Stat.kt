package com.github.kerubistan.kerub.utils.junix.procfs

import com.github.kerubistan.kerub.host.bashMonitor
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

object Stat : AbstractProcFs {

	private const val separator = "--separator"
	private val fieldSeparator = "\\s+".toRegex()

	class CpuLoadMonitorOutputStream(val handler: (Map<String, CpuStat>) -> Unit) : OutputStream() {
		private val buffer = StringBuilder()
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
		session.bashMonitor("grep cpu /proc/stat", 1, separator, CpuLoadMonitorOutputStream(handler))
	}

	fun cpuLoadMonitorIncremental(session: ClientSession, handler: (Map<String, CpuStat>) -> Unit) {
		var previous: Map<String, CpuStat>? = null
		cpuLoadMonitor(session) {
			newStats ->
			synchronized(this) {
				if (previous != null) {
					handler(previous!!.map {
						val prev = requireNotNull(previous!![it.key])
						val curr = newStats.getValue(it.key)
						it.key to curr.copy(
								user = curr.user - prev.user,
								system = curr.system - prev.system,
								idle = curr.idle - prev.idle
						)
					}.toMap()
					)
				}
				previous = newStats
			}
		}
	}
}
