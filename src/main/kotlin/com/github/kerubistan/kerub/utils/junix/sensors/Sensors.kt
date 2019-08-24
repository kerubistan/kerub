package com.github.kerubistan.kerub.utils.junix.sensors

import com.github.kerubistan.kerub.host.bashMonitor
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.host.process
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.common.Ubuntu
import io.github.kerubistan.kroki.strings.substringBetween
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream

object Sensors : OsCommand {

	override fun providedBy(): List<Pair<(SoftwarePackage) -> Boolean, List<String>>> =
			listOf(
					// TODO get teh package name from other distributions
					{ pack: SoftwarePackage -> pack.name == "lm-sensors" } to listOf(Ubuntu)
			)

	override fun available(hostCapabilities: HostCapabilities?) =
			// lm-sensors only for linux
			hostCapabilities?.os == OperatingSystem.Linux && super.available(hostCapabilities)

	private const val separator = "---end---"

	private fun parseOutput(output: String) = output.lines().filter { it.startsWith("Core ") }
			.map {
				CpuTemperatureInfo(
						coreId = it.substringBetween("Core ", ":").toInt(),
						temperature = it.substringBetween(":", ".").trim().toInt(),
						high = it.substringBetween("high =", ".").trim().toInt(),
						critical = it.substringBetween("crit =", ".").trim().toInt()
				)
			}.sortedBy { it.coreId }

	fun senseCpuTemperatures(session: ClientSession) = parseOutput(session.executeOrDie("sensors"))

	class SensorsOutputStream(val handler: (List<CpuTemperatureInfo>) -> Unit) : OutputStream() {

		private val buff = StringBuilder()
		override fun write(data: Int) {
			buff.append(data.toChar())
			if(buff.endsWith(separator)) {
				handler(parseOutput(buff.toString().substringBefore(separator)))
				buff.clear()
			}
		}
	}

	fun monitorCpuTemperatures(session: ClientSession, interval: Int = 1, handler: (List<CpuTemperatureInfo>) -> Unit) {
		session.bashMonitor("sensors", interval, separator, SensorsOutputStream(handler))
	}

}