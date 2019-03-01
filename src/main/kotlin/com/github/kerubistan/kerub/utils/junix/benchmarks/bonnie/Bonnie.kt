package com.github.kerubistan.kerub.utils.junix.benchmarks.bonnie

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession

object Bonnie : OsCommand {

	override fun available(osVersion: SoftwarePackage, packages: List<SoftwarePackage>) =
			packages.any { it.name == "bonnie++" || it.name == "bonnieplus" }

	private val coma = ",".toRegex()

	private fun String.kPerSec() = (this.toLong() * 1024).toBigInteger()

	private fun String.time() = when {
		this.endsWith("us") -> this.substringBefore("us").toLong()
		this.endsWith("ms") -> this.substringBefore("ms").toLong() * 1000
		else -> TODO("not handled: $this")
	}

	fun run(session: ClientSession, directory: String, user: String = "root", nrOfFiles: Int = 128): FsBenchmarkData =
			session.executeOrDie("bonnie++ -n $nrOfFiles -u $user -d $directory", isError = { false })
					.lines()
					.last { it.isNotBlank() }
					.split(coma).let { result ->
						FsBenchmarkData(
								sequentialOutputPerChr = IoBenchmarkItem(
										throughput = result[7].kPerSec(),
										latency = result[36].time(),
										cpuUsagePercent = result[8].toShort()
								),
								sequentialOutputPerBlock = IoBenchmarkItem(
										throughput = result[9].kPerSec(),
										latency = result[37].time(),
										cpuUsagePercent = result[10].toShort()
								),
								sequentialOutputRewrite = IoBenchmarkItem(
										throughput = result[11].kPerSec(),
										latency = result[38].time(),
										cpuUsagePercent = result[12].toShort()
								),
								sequentialInputPerChr = IoBenchmarkItem(
										throughput = result[13].kPerSec(),
										latency = result[39].time(),
										cpuUsagePercent = result[14].toShort()
								),
								sequentialInputPerBlock = IoBenchmarkItem(
										throughput = result[15].kPerSec(),
										latency = result[40].time(),
										cpuUsagePercent = result[16].toShort()
								),
								random = IoBenchmarkItem(
										throughput = result[17].toDouble().toBigDecimal().toBigInteger(),
										latency = result[41].time(),
										cpuUsagePercent = result[18].toShort()
								)
						)
					}

}