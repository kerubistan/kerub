package com.github.K0zka.kerub.utils.junix.lspci

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.hardware.PciDevice
import com.github.K0zka.kerub.utils.getLogger
import org.apache.sshd.client.session.ClientSession

object LsPci {
	val logger = getLogger(LsPci::class)
	@JvmStatic val doublequote = "\""

	fun execute(session: ClientSession): List<PciDevice> {
		return parse (session.execute("lspci -mm"))
	}

	@JvmStatic fun parse(output: String): List<PciDevice> =
			output.trim().split("\n").toList().map { parseLine(it) }

	@JvmStatic fun parseLine(line: String): PciDevice {
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