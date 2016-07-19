package com.github.K0zka.kerub.utils.junix.vmstat

import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.toSize
import org.apache.commons.io.input.NullInputStream
import org.apache.commons.io.output.NullOutputStream
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream
import java.io.Serializable
import java.math.BigInteger
import java.util.regex.Pattern

object VmStat : OsCommand {

	val someSpaces = Pattern.compile("\\s+")

	class VmstatOutputStream(val handler: (VmStatEvent) -> Unit) : OutputStream() {
		override fun write(data: Int) {
			if (data == 10) {

				val line = buff.toString().trim()
				buff.setLength(0)
				if (line.startsWith("procs") || line.startsWith("r")) {
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
						),
						swapMem = "${split[2]} kb".toSize(),
						freeMem = "${split[3]} kb".toSize(),
						ioBuffMem = "${split[4]} kb".toSize(),
						cacheMem = "${split[5]} kb".toSize()
				)
				)
			} else {
				buff.append(data.toChar())
			}
		}

		val buff: StringBuilder = StringBuilder(128)
	}

	fun vmstat(session: ClientSession, handler: (VmStatEvent) -> Unit, delay: Int = 1): Unit {
		val exec = session.createExecChannel("vmstat ${delay}")
		exec.`in` = NullInputStream(0)
		exec.err = NullOutputStream()
		exec.out = VmstatOutputStream(handler)
		exec.open().verify()
	}
}