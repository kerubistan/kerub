package com.github.K0zka.kerub.utils.junix.lspci

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.PciDevice
import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.ClientSession
import kotlin.platform.platformStatic

public object LsPci {
	val logger = getLogger(LsPci::class)
	platformStatic val doublequote = "\""

	fun execute(session: ClientSession): List<PciDevice> {
		return parse (session.execute("lspci -mm"))
	}

	internal fun parse(output: String): List<PciDevice> =
			output.split("\n").toList().map { parseLine(it) }

	internal fun parseLine(line: String): PciDevice {
		return PciDevice(
				address = line.substringBefore(" "),
				vendor = line.substringAfter(doublequote)
						.substringAfter(doublequote)
						.substringAfter(doublequote)
						.substringBefore(doublequote),
				devClass = line.substringAfter(doublequote)
						.substringBefore(doublequote),
		        device = line.substringAfter(doublequote)
				        .substringAfter(doublequote)
				        .substringAfter(doublequote)
				        .substringAfter(doublequote)
				        .substringAfter(doublequote)
				        .substringBefore(doublequote)
		                )
	}
}