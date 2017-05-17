package com.github.K0zka.kerub.utils.ipmi

import nl.komponents.kovenant.then

object IpmiPing {
	@JvmStatic fun main(args: Array<String>) {
		val address = requireNotNull(args.getOrNull(0)) { "address needed" }
		val times = (args.getOrNull(1) ?: "1").toInt()
		(1..times).forEach {
			IpmiClient().sendPing(address, 1).then({ println("ok") }).fail({ println("fail") }).get()
		}
	}
}