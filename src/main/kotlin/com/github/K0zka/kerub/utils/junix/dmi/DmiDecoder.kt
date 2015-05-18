package com.github.K0zka.kerub.utils.junix.dmi

import kotlin.platform.platformStatic
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import java.math.BigDecimal
import com.github.K0zka.kerub.model.hardware.CacheInformation
import com.github.K0zka.kerub.model.hardware.ChassisInformation
import java.util.HashMap
import com.github.K0zka.kerub.model.hardware.SystemInformation
import java.util.UUID
import com.github.K0zka.kerub.model.hardware.MemoryInformation


public class DmiDecoder {

	class object {
		platformStatic fun split(input: String): List<String> =
				input.split("\n\n") //empty line
						.filter { it.startsWith("Handle 0x") }

		platformStatic fun type(input: String): Int =
				input.substringBetween("DMI type ", ",").toInt()

		platformStatic fun handle(input: String): String =
				input.substringBetween("Handle ", ",")

		platformStatic val mappers: Map<Int, (String, Map<String, Any>) -> Any> = mapOf(
				1 to {(input: String, dependencies: Map<String, Any>): Any ->
					SystemInformation(
							manufacturer = input.substringBetween("Manufacturer: ", "\n"),
					        family = input.substringBetween("Family: ", "\n"),
					        version = input.substringBetween("Version: ", "\n"),
					        uuid = UUID.fromString (input.substringBetween("UUID: ", "\n"))
					                 )
				},
				3 to {(input: String, dependencies: Map<String, Any>): Any ->
					ChassisInformation(
							manufacturer = input.substringBetween("Manufacturer: ", "\n"),
							height = input.optionalIntBetween("Height: ", "\n"),
							nrOfPowerCords = input.intBetween("Number Of Power Cords: ", "\n"),
							type = input.substringBetween("Type: ", "\n")
					                  )
				},
				4 to {(input: String, dependencies: Map<String, Any>): Any ->
					ProcessorInformation(
							manufacturer = input.substringBetween("Manufacturer: ", "\n"),
							coreCount = input.intBetween("Core Count: ", "\n"),
							threadCount = input.intBetween("Thread Count: ", "\n"),
							maxSpeedMhz = input.intBetween("Max Speed: ", " MHz"),
							socket = input.substringBetween("Socket Designation: ", "\n"),
							version = input.substringBetween("Version: ", "\n"),
							voltage = BigDecimal(input.substringBetween("Voltage: ", " V")),
							l1cache = dependencies[input.substringBetween("L1 Cache Handle: ", "\n")] as CacheInformation?,
							l2cache = dependencies[input.substringBetween("L2 Cache Handle: ", "\n")] as CacheInformation?,
							l3cache = dependencies[input.substringBetween("L3 Cache Handle: ", "\n")] as CacheInformation?,
							flags = listOf()
					                    )
				},
				7 to {(input: String, dependencies: Map<String, Any>): Any ->
					CacheInformation(
							socket = input.substringBetween("Socket Designation: ", "\n"),
							errorCorrection = input.substringBetween("Error Correction Type: ", "\n"),
							sizeKb = input.intBetween("Installed Size: ", " kB\n"),
							operation = input.substringBetween("Operational Mode: ", "\n"),
							speedNs = input.optionalIntBetween("Speed: ", " ns")
					                )
				},
		        17 to {(input: String, dependencies: Map<String, Any>): Any ->
			        MemoryInformation(
					        sizeMb = input.intBetween("Size: "," MB"),
			                manufacturer = input.substringBetween("Manufacturer: ", "\n"),
			                type = input.substringBetween("Type: ", "\n"),
			                bankLocator = input.substringBetween("Bank Locator: ", "\n"),
			                configuredSpeedMhz = input.intBetween("Configured Clock Speed: "," MHz"),
			                formFactor = input.substringBetween("Form Factor: ","\n"),
			                locator = input.substringBetween("Locator: ","\n"),
			                partNumber = input.substringBetween("Part Number: ", "\n"),
			                serialNumber = input.substringBetween("Serial Number: ","\n"),
			                speedMhz = input.intBetween("Speed: ", " MHz")
			                         )

		        }
		                                                                               )

		platformStatic val resolutionOrder = array(17, 7, 3, 4, 1)

		platformStatic fun parse(input: String) : Map<String, Any>{
			val records = split(input)
			val recordsByType = records.groupBy { type(it) }
			val recordsByHandle = HashMap<String, Any>()
			for (type in resolutionOrder) {
				val recordsOfType = recordsByType[type]
				for (record in recordsOfType ?: listOf()) {
					val resolver = mappers[type]!!
					recordsByHandle.put(handle(record), resolver(record.concat("\n") , recordsByHandle))
				}
			}
			return recordsByHandle
		}

	}

}