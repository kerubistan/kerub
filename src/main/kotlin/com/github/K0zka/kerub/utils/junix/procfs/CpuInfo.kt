package com.github.K0zka.kerub.utils.junix.procfs

import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.substringBetween
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession

object CpuInfo : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?): Boolean =
			hostCapabilities?.os == OperatingSystem.Linux

	internal fun value(properties : String, property : String) =
			properties.substringBetween(property,"\n").substringAfter(":").trim()

	fun list(session: ClientSession): List<CpuInfoRecord> =
			session.createSftpClient().use {
				sftp ->
				sftp.read("/proc/cpuinfo").reader(Charsets.US_ASCII).use {
					reader ->
					val text = reader.readText()
					text.split("\n\n").filter { it.isNotBlank() }.map {
						CpuInfoRecord(
								vendorId = value(it, "vendor_id"),
								cacheSize = value(it, "cache size").toSize(),
								cpuFamily = value(it, "cpu family").toInt(),
								mhz = value(it, "cpu MHz").toFloat(),
								modelId = value(it, "model").toInt(),
								nr = value(it, "processor").toInt(),
								modelName = value(it, "model name"),
								flags = value(it, "flags").split(" ").toList()
						)
					}
				}
			}
}