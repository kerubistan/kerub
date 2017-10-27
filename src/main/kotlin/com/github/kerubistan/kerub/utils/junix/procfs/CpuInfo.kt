package com.github.kerubistan.kerub.utils.junix.procfs

import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.substringBetween
import com.github.kerubistan.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object CpuInfo : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?): Boolean =
			hostCapabilities?.os == OperatingSystem.Linux

	internal fun value(properties: String, property: String) =
			properties.substringBetween(property, "\n").substringAfter(":").trim()

	fun listPpc(session: ClientSession): List<CpuInfoRecord> =
			session.createSftpClient().use {
				sftp ->
				sftp.read("/proc/cpuinfo").reader(Charsets.US_ASCII).use {
					reader ->
					val text = reader.readText()
					val sections = text.split("\n\n").filter { it.isNotBlank() }
					val archInfo = sections.last()
					val model = value(archInfo, "model")
					sections.minus(archInfo).map {
						CpuInfoRecord(
								vendorId = model,
								cacheSize = BigInteger.ZERO,
								cpuFamily = 0,
								mhz = value(it, "clock").substringBefore("MHz").toFloat(),
								modelId = 0,
								nr = value(it, "processor").toInt(),
								modelName = value(it, "cpu"),
								flags = listOf()
						)
					}
				}
			}


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