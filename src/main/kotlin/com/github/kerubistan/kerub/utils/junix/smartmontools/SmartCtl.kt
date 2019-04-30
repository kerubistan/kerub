package com.github.kerubistan.kerub.utils.junix.smartmontools

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.host.process
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.equalsAnyIgnoreCase
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.remove
import com.github.kerubistan.kerub.utils.substringBetween
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream
import java.math.BigInteger.ZERO

object SmartCtl : OsCommand {

	override fun available(hostCapabilities: HostCapabilities?): Boolean =
			hostCapabilities?.index?.installedSoftwareByName?.containsKey("smartmontools") ?: false

	private const val delimiter = ":"

	private const val separator = "---end---"

	private fun parseInfoOutput(output: String): StorageDevice {
		val properties =
				output.substringAfter("=== START OF INFORMATION SECTION ===").lines().filter(String::isNotBlank)
						.associate {
							it.substringBefore(delimiter) to it.substringAfter(delimiter).trim()
						}
		return StorageDevice(
				modelFamily = properties["Model Family"] ?: "",
				deviceModel = properties["Device Model"] ?: "",
				firmwareVersion = properties[""] ?: "",
				rotationRate = properties["Rotation Rate"]?.let {
					if (it == "Solid State Device") {
						null
					} else {
						it.substringBefore(" rpm").toInt()
					}
				},
				userCapacity = properties["User Capacity"]?.substringBefore(" bytes")?.remove("([,'])".toRegex())?.toBigInteger()
						?: ZERO,
				sataVersion = properties["SATA Version is"]?.substringBetween("SATA ", ",") ?: "",
				serialNumber = properties["Serial Number"] ?: ""
		)
	}

	fun info(session: ClientSession, device: String) =
			parseInfoOutput(session.executeOrDie("smartctl -i $device"))

	class SmartCtlMonitorOutputStream(val update: (Boolean) -> Unit) : OutputStream() {
		private val buffer = StringBuilder()
		override fun write(data: Int) {
			buffer.append(data.toChar())
			if (buffer.endsWith(separator)) {
				update(parseCheckResult(buffer.toString().substringBefore(separator)))
				buffer.clear()
			}
		}

	}

	fun monitor(session: ClientSession, device: String, interval: Int = 60, update: (Boolean) -> Unit) =
			session.process(
					"""bash -c " while true; do smartctl -H $device; echo $separator; sleep $interval; done" """,
					SmartCtlMonitorOutputStream(update))

	private fun parseCheckResult(result: String) = result.lines().last(String::isNotEmpty)
			.substringAfter(":").trim().equalsAnyIgnoreCase("ok", "passed")

	fun healthCheck(session: ClientSession, device: String) =
			parseCheckResult(session.executeOrDie("smartctl -H $device"))


}