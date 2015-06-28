package com.github.K0zka.kerub.utils.junix.vmstat

import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.ClientSession
import org.jetbrains.kotlin.utils.sure
import java.io.OutputStream
import java.io.Serializable
import java.util.regex.Pattern
import kotlin.text.Regex

public object VmStat {

	public data class IoStatistic (
			read : Int,
	        write : Int
	                       ) : Serializable

	public data class VmStatEvent (
		userCpu : Byte,
	    systemCpu : Byte,
	    idleCpu : Byte,
	    iowaitCpu : Byte,
	    swap : IoStatistic,
	    block : IoStatistic
	                       ) : Serializable

	val someSpaces = Pattern.compile("\\s+")

	class VmstatOutputStream(val handler : (VmStatEvent) -> Unit) : OutputStream() {
		override fun write(data: Int) {
			if(data == 10) {

				val line = buff.toString().trim()
				buff.setLength(0)
				if(line.startsWith("procs") || line.startsWith("r")) {
					return
				}
				val split = line.split(someSpaces)

				handler(VmStatEvent(
						userCpu = split[12].toInt().toByte(),
				        systemCpu = split[13].toInt().toByte(),
				        idleCpu = split[14].toInt().toByte(),
						iowaitCpu = split[15].toInt().toByte(),
				        swap = IoStatistic(
						        read = split[6].toInt(),
				                write = split[7].toInt()
				                          ),
				        block = IoStatistic(
						        read = split[8].toInt(),
				                write = split[9].toInt()
				                           )
				                   ))
			} else {
				buff.append(data.toChar())
			}
		}

		val buff : StringBuilder = StringBuilder(128)
	}

	fun vmstat(session : ClientSession, handler : (VmStatEvent) -> Unit, delay : Int = 1) : Unit {
		val exec = session.createExecChannel("vmstat ${delay}")
		exec.setIn(NullInputStream(0))
		exec.setErr(NullOutputStream())
		exec.setOut( VmstatOutputStream(handler) )
		exec.open().verify()
	}
}