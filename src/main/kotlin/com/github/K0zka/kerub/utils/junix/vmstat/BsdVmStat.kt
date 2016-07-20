package com.github.K0zka.kerub.utils.junix.vmstat

import com.github.K0zka.kerub.utils.toBigInteger
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object BsdVmStat {
	class BsdVmstatOutputStream(val handler: (BsdVmStatEvent) -> Unit) : AbstractVmstatOutputStream() {
		override fun handleInput(split: List<String>) {
			handler(
					BsdVmStatEvent(
							userCpu = split[16].toByte(),
							systemCpu = split[17].toByte(),
							idleCpu = split[18].toByte(),
							cacheMem = BigInteger.ZERO,
							ioBuffMem = BigInteger.ZERO,
							swapMem = BigInteger.ZERO,
							freeMem = split[4].toBigInteger()
					)
			)
		}

	}

	fun vmstat(session: ClientSession, handler: (BsdVmStatEvent) -> Unit, delay: Int = 1): Unit {
		commonVmStat(
				session = session,
				delay = delay,
				out = BsdVmstatOutputStream(handler)
		)
	}

}