package com.github.K0zka.kerub.utils.junix.vmstat

import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.util.regex.Pattern

object VmStat : OsCommand {

	val someSpaces = Pattern.compile("\\s+")

	class VmstatOutputStream(val handler: (VmStatEvent) -> Unit) : AbstractVmstatOutputStream() {

		override fun handleInput(split: List<String>) {
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
		}

	}

	fun vmstat(session: ClientSession, handler: (VmStatEvent) -> Unit, delay: Int = 1): Unit {
		commonVmStat(
				session = session,
				delay = delay,
				out = VmstatOutputStream(handler)
		)
	}
}