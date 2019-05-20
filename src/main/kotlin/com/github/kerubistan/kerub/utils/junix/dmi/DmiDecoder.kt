package com.github.kerubistan.kerub.utils.junix.dmi

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.model.hardware.CacheInformation
import com.github.kerubistan.kerub.model.hardware.ChassisInformation
import com.github.kerubistan.kerub.model.hardware.MemoryArrayInformation
import com.github.kerubistan.kerub.model.hardware.MemoryInformation
import com.github.kerubistan.kerub.model.hardware.ProcessorInformation
import com.github.kerubistan.kerub.model.hardware.SystemInformation
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.toSize
import io.github.kerubistan.kroki.strings.substringBetween
import org.apache.sshd.client.session.ClientSession
import java.util.HashMap
import java.util.UUID

object DmiDecoder : OsCommand {

	fun run(session: ClientSession) = parse(session.executeOrDie("dmidecode"))

	private val logger = getLogger(DmiDecoder::class)

	fun split(input: String): List<String> =
			input.split("\n\n".toRegex()).toTypedArray() //empty line
					.filter { it.startsWith("Handle 0x") }

	fun type(input: String): Int =
			input.substringBetween("DMI type ", ",").toInt()

	fun handle(input: String): String =
			input.substringBetween("Handle ", ",")

	private const val manufacturer = "Manufacturer:"

	private fun String.manufacturer() = this.substringBetween(manufacturer, "\n").trim()

	val mappers: Map<Int, (String, Map<String, Any>) -> Any> = mapOf(
			1 to { input, _ ->
				SystemInformation(
						manufacturer = input.manufacturer(),
						family = input.substringBetween("Family:", "\n").trim(),
						version = input.substringBetween("Version:", "\n").trim(),
						uuid = UUID.fromString(input.substringBetween("UUID: ", "\n"))
				)
			},
			3 to { input, _ ->
				ChassisInformation(
						manufacturer = input.manufacturer(),
						height = input.optionalIntBetween("Height: ", "\n"),
						nrOfPowerCords = input.optionalIntBetween("Number Of Power Cords: ", "\n"),
						type = input.substringBetween("Type: ", "\n")
				)
			},
			4 to { input, dependencies ->
				ProcessorInformation(
						manufacturer = input.manufacturer(),
						coreCount = input.optionalIntBetween("Core Count: ", "\n"),
						threadCount = input.optionalIntBetween("Thread Count: ", "\n"),
						maxSpeedMhz = input.optionalIntBetween("Max Speed: ", " MHz\n"),
						socket = input.substringBetween("Socket Designation: ", "\n"),
						version = input.substringBetween("Version: ", "\n"),
						voltage = input.bigDecimalBetween("Voltage: ", " V\n"),
						l1cache = dependencies[input.substringBetween(
								"L1 Cache Handle: ",
								"\n")] as CacheInformation?,
						l2cache = dependencies[input.substringBetween(
								"L2 Cache Handle: ",
								"\n")] as CacheInformation?,
						l3cache = dependencies[input.substringBetween(
								"L3 Cache Handle: ",
								"\n")] as CacheInformation?,
						flags = listOf()
				)
			},
			7 to { input, _ ->
				CacheInformation(
						socket = input.substringBetween("Socket Designation: ", "\n"),
						errorCorrection = input.substringBetween("Error Correction Type: ", "\n"),
						size = input.substringBetween("Installed Size: ", "\n").trim().toSize().toInt(),
						operation = input.substringBetween("Operational Mode: ", "\n"),
						speedNs = input.optionalIntBetween("Speed: ", " ns")
				)
			},
			16 to { input, _ ->
				MemoryArrayInformation(
						maxCapacity = input.substringBetween("Maximum Capacity:", "\n").toSize(),
						errorCorrection = input.substringBetween("Error Correction Type:", "\n").trim(),
						location = input.substringBetween("Location:", "\n").trim()
				)
			},
			17 to { input, _ ->
				MemoryInformation(
						size = input.substringBetween("Size: ", "\n").toSize(),
						manufacturer = input.manufacturer(),
						type = input.substringBetween("Type: ", "\n"),
						bankLocator = input.substringBetween("Bank Locator: ", "\n"),
						configuredSpeedMhz = input.optionalIntBetween("Configured Clock Speed: ", " MHz"),
						formFactor = input.substringBetween("Form Factor: ", "\n"),
						locator = input.substringBetween("Locator: ", "\n"),
						partNumber = input.substringBetween("Part Number: ", "\n"),
						serialNumber = input.substringBetween("Serial Number: ", "\n"),
						speedMhz = input.optionalIntBetween("Speed: ", " MHz")
				)

			}
	)

	private val resolutionOrder = arrayOf(16, 17, 7, 3, 4, 1)

	fun parse(input: String): Map<String, Any> {
		val records = split(input)
		val recordsByType = records.groupBy { type(it) }
		val recordsByHandle = HashMap<String, Any>()
		for (type in resolutionOrder) {
			val recordsOfType = recordsByType[type]
			for (record in recordsOfType ?: listOf()) {
				val resolver = mappers.getValue(type)
				try {
					recordsByHandle[handle(record)] = resolver(record + "\n", recordsByHandle)
				} catch (iae: IllegalArgumentException) {
					logger.warn("Structure could not be parsed: {}", record, iae)
				}
			}
		}
		return recordsByHandle
	}

}
