package com.github.kerubistan.kerub.utils.junix.dmi

import com.github.kerubistan.kerub.model.hardware.ChassisInformation
import com.github.kerubistan.kerub.model.hardware.MemoryArrayInformation
import com.github.kerubistan.kerub.model.hardware.MemoryInformation
import com.github.kerubistan.kerub.model.hardware.ProcessorInformation
import com.github.kerubistan.kerub.model.hardware.SystemInformation
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.KB
import io.github.kerubistan.kroki.size.MB
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DmiDecoderTest {
	@Test
	fun split() {
		val handles = DmiDecoder.split(mylaptop)
		assert(handles.size == 0x33)
	}

	@Test
	fun type() {
		assert(
				DmiDecoder.type(
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

		assert(
				DmiDecoder.type(
						"""
Handle 0x002B, DMI type 21, 7 bytes
Built-in Pointing Device
	Type: Touch Pad
	Interface: PS/2
	Buttons: 4
"""
				) == 21)

	}

	@Test
	fun handle() {
		assert(
				DmiDecoder.handle(
						"""
Handle 0x002B, DMI type 21, 7 bytes
Built-in Pointing Device
	Type: Touch Pad
	Interface: PS/2
	Buttons: 4
""") == "0x002B")
	}

	@Test
	fun parse() {
		val devices = DmiDecoder.parse(mylaptop)
		val system = devices["0x0001"] as SystemInformation
		assertEquals("IDEAPAD", system.family)
		assertEquals("Lenovo G585", system.version)
		assertEquals("LENOVO", system.manufacturer)
		val processor = devices["0x0004"] as ProcessorInformation
		assertEquals("AMD processor", processor.manufacturer)
		assertEquals(2, processor.coreCount)
		assertEquals(2, processor.threadCount)
	}

	@Test
	fun parseWithNuc() {
		val devices = DmiDecoder.parse(nuc)
		val system = devices["0x0001"] as SystemInformation
		assert(system.family == "")
		assert(system.version == "")
		assert(system.manufacturer == "")
		assert(system.uuid == UUID.fromString("3DACA680-34DC-11E1-988E-C03FD56F97FC"))
		val processor = devices["0x003A"] as ProcessorInformation
		assertEquals("Intel(R) Corp.", processor.manufacturer)
		assertEquals("Intel(R) Core(TM) i3-4010U CPU @ 1.70GHz", processor.version)
		assertEquals(2, processor.coreCount)
		assertEquals(4, processor.threadCount)

		val l1Cache = processor.l1cache!!
		assertEquals(128.KB.toInt(), l1Cache.size)
		assertNull(l1Cache.speedNs)
		assertEquals("Single-bit ECC", l1Cache.errorCorrection)

		val l2Cache = processor.l2cache!!
		assertEquals(512.KB.toInt(), l2Cache.size.toInt())
		assertNull(l2Cache.speedNs)
		assertEquals("Single-bit ECC", l2Cache.errorCorrection)

		val l3Cache = processor.l3cache!!
		assertEquals(3072.KB.toInt(), l3Cache.size)
		assertNull(l3Cache.speedNs)
		assertEquals("Single-bit ECC", l3Cache.errorCorrection)

		val memArray = devices["0x003E"] as MemoryArrayInformation
		assertEquals(memArray.maxCapacity, 16.GB)
		assertEquals("None", memArray.errorCorrection)
	}

	@Test
	fun parseWithQemu() {
		val devices = DmiDecoder.parse(qemuKvm)
		val systemInfo = devices["0x0100"] as SystemInformation
		assert(systemInfo.manufacturer == "QEMU")
		assert(systemInfo.version == "pc-i440fx-2.1")
		assert(systemInfo.uuid == UUID.fromString("99163626-EDCF-FB4C-A81C-A7FD9EAA058E"))

		val chassisInfo = devices["0x0300"] as ChassisInformation
		assertEquals("QEMU", chassisInfo.manufacturer)
		assert(chassisInfo.nrOfPowerCords == null)
		assertEquals("Other", chassisInfo.type)

		val procInfo = devices["0x0400"] as ProcessorInformation
		assertEquals("QEMU", procInfo.manufacturer)
		assertEquals("pc-i440fx-2.1", procInfo.version)
		assertEquals(1, procInfo.coreCount)
		assertNull(procInfo.l1cache)
		assertNull(procInfo.l2cache)
		assertNull(procInfo.l3cache)

		val memInfo = devices["0x1100"] as MemoryInformation
		assertEquals("QEMU", memInfo.manufacturer)
		assertNull(memInfo.speedMhz)
		assertEquals("DIMM", memInfo.formFactor)
	}

	@Test
	fun parseWithNonGnu() {
		//a totally scrapped output
		DmiDecoder.parse(nongnu)
	}

	@Test
	fun parseBochsBios() {
		val devices = DmiDecoder.parse(kvmQemuBochs)

		val cpuInfo = devices["0x0401"] as ProcessorInformation
		assertEquals("Bochs", cpuInfo.manufacturer)
		assertEquals(2000, cpuInfo.maxSpeedMhz)

		val memoryDevice = devices["0x1100"] as MemoryInformation
		assertEquals("DIMM", memoryDevice.formFactor)
		assertEquals(2048.MB, memoryDevice.size)

		val chassisInfo = devices["0x0300"] as ChassisInformation
		assertEquals("Bochs", chassisInfo.manufacturer)
		assertNull(chassisInfo.nrOfPowerCords)
	}
}