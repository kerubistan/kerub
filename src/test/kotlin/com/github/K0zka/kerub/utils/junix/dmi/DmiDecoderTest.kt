package com.github.K0zka.kerub.utils.junix.dmi

import com.github.K0zka.kerub.model.hardware.ChassisInformation
import com.github.K0zka.kerub.model.hardware.MemoryInformation
import org.junit.Test
import com.github.K0zka.kerub.model.hardware.SystemInformation
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import java.util.UUID

public class DmiDecoderTest {
	Test
	fun split() {
		val handles = DmiDecoder.split(mylaptop);
		assert(handles.size() == 0x33)
	}

	Test
	fun type() {
		assert(DmiDecoder.type(
				"""
Handle 0x002F, DMI type 41, 11 bytes
Onboard Device
	Reference Designation: 82567LM Gigabit Network Connection
	Type: Ethernet
	Status: Enabled
	Type Instance: 1
	Bus Address: 0000:00:00.1
"""
		                      ) == 41)

		assert(DmiDecoder.type(
				"""
Handle 0x002B, DMI type 21, 7 bytes
Built-in Pointing Device
	Type: Touch Pad
	Interface: PS/2
	Buttons: 4
"""
		                      ) == 21)

	}

	Test
	fun handle() {
		assert(DmiDecoder.handle("""
Handle 0x002B, DMI type 21, 7 bytes
Built-in Pointing Device
	Type: Touch Pad
	Interface: PS/2
	Buttons: 4
""") == "0x002B")
	}

	Test
	fun parse() {
		val devices = DmiDecoder.parse(mylaptop)
		val system = devices["0x0001"] as SystemInformation
		assert(system.family == "IDEAPAD")
		assert(system.version == "Lenovo G585")
		assert(system.manufacturer == "LENOVO")
		val processor = devices["0x0004"] as ProcessorInformation
		assert(processor.manufacturer == "AMD processor")
		assert(processor.coreCount == 2)
		assert(processor.threadCount == 2)
	}

	Test
	fun parseWithNuc() {
		val devices = DmiDecoder.parse(nuc)
		val system = devices["0x0001"] as SystemInformation
		assert(system.family == "")
		assert(system.version == "")
		assert(system.manufacturer == "")
		assert(system.uuid == UUID.fromString("3DACA680-34DC-11E1-988E-C03FD56F97FC"))
		val processor = devices["0x003A"] as ProcessorInformation
		assert(processor.manufacturer == "Intel(R) Corp.")
		assert(processor.version == "Intel(R) Core(TM) i3-4010U CPU @ 1.70GHz")
		assert(processor.coreCount == 2)
		assert(processor.threadCount == 4)

		val l1Cache = processor.l1cache!!
		assert(l1Cache.sizeKb == 128)
		assert(l1Cache.speedNs == null)
		assert(l1Cache.errorCorrection == "Single-bit ECC")

		val l2Cache = processor.l2cache!!
		assert(l2Cache.sizeKb == 512)
		assert(l2Cache.speedNs == null)
		assert(l2Cache.errorCorrection == "Single-bit ECC")

		val l3Cache = processor.l3cache!!
		assert(l3Cache.sizeKb == 3072)
		assert(l3Cache.speedNs == null)
		assert(l3Cache.errorCorrection == "Single-bit ECC")
	}

	Test
	fun parseWithQemu() {
		val devices = DmiDecoder.parse(qemuKvm)
		val systemInfo = devices["0x0100"] as SystemInformation
		assert(systemInfo.manufacturer == "QEMU")
		assert(systemInfo.version == "pc-i440fx-2.1")
		assert(systemInfo.uuid == UUID.fromString("99163626-EDCF-FB4C-A81C-A7FD9EAA058E"))

		val chassisInfo = devices["0x0300"] as ChassisInformation
		assert(chassisInfo.manufacturer == "QEMU")
		assert(chassisInfo.nrOfPowerCords == null)
		assert(chassisInfo.type == "Other")

		val procInfo = devices["0x0400"] as ProcessorInformation
		assert(procInfo.manufacturer == "QEMU")
		assert(procInfo.version == "pc-i440fx-2.1")
		assert(procInfo.coreCount == 1)
		assert(procInfo.l1cache == null)
		assert(procInfo.l2cache == null)
		assert(procInfo.l3cache == null)

		val memInfo = devices["0x1100"] as MemoryInformation
		assert(memInfo.manufacturer == "QEMU")
		assert(memInfo.speedMhz == null)
		assert(memInfo.formFactor == "DIMM")
	}

	Test
	fun parseWithNonGnu() {
		//a totally scrapped output
		DmiDecoder.parse(nongnu)
	}

	Test
	fun parseBochsBios() {
		val devices = DmiDecoder.parse(kvmQemuBochs)

		val cpuInfo = devices["0x0401"] as ProcessorInformation
		assert(cpuInfo.manufacturer == "Bochs")
		assert(cpuInfo.maxSpeedMhz == 2000)

		val memoryDevice = devices["0x1100"] as MemoryInformation
		assert(memoryDevice.formFactor == "DIMM")
		assert(memoryDevice.sizeMb == 2048)

		val chassisInfo = devices["0x0300"] as ChassisInformation
		assert(chassisInfo.manufacturer == "Bochs")
		assert(chassisInfo.nrOfPowerCords == null)
	}
}