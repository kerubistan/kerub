package com.github.kerubistan.kerub.utils.junix.lspci

import com.github.kerubistan.kerub.host.execute
import com.github.kerubistan.kerub.model.hardware.PciDevice
import org.apache.sshd.client.session.ClientSession

object LsPci {
	@JvmStatic val doublequote = "\""

	fun execute(session: ClientSession): List<PciDevice> {
		return parse(session.execute("lspci -mm"))
	}

	@JvmStatic fun parse(output: String): List<PciDevice> =
			output.trim().lines().toList().map { parseLine(it) }

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