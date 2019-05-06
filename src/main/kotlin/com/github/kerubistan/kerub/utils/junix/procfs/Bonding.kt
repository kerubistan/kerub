package com.github.kerubistan.kerub.utils.junix.procfs

import com.github.kerubistan.kerub.host.getFileContents
import org.apache.sshd.client.session.ClientSession

object Bonding : AbstractProcFs {

	private val modes = mapOf(
			"load balancing" to BondingMode.LoadBalancing,
			"active backup" to BondingMode.ActiveBackup
	)

	fun listBondInterfaces(session: ClientSession) = session.createSftpClient().use { sftp ->
		sftp.open("/proc/net/bonding").use { handle -> sftp.listDir(handle).map { it.filename } }
	}

	fun getBondInfo(session: ClientSession, bondInterface: String): BondingInfo {
		val sections = session.getFileContents("/proc/net/bonding/$bondInterface")
				.split("\n\n")
				.map { it.lines().map { it.substringBefore(":") to it.substringAfter(":").trim() }.toMap() }
		check(sections.size >= 3) {
			"/proc/net/bonding/$bondInterface is expected to have at least 3 sections (it has ${sections.size})"
		}
		val bondingModeStr = requireNotNull(sections[1]["Bonding Mode"]) { "Bonding Mode required" }
				.substringBefore("(").trim()
		return BondingInfo(
				mode = modes.getOrElse(bondingModeStr) {
					throw IllegalArgumentException("$bondingModeStr not handled")
				},
				slaves = sections.subList(fromIndex = 2, toIndex = sections.size).map { section ->
					SlaveInterface(
							name = requireNotNull(section["Slave Interface"]) { "Slave Interface name required in $section" },
							duplex = section["Duplex"] == "full",
							hardwareAddress = requireNotNull(section["Permanent HW addr"]) { "Permanent HW addr required in $section" },
							speedMbps = section["Speed"]?.substringBefore(" ")?.let {
								if (it == "Unknown") null else it.toInt()
							}
					)
				}
		)
	}
}