package com.github.kerubistan.kerub.utils.junix.sensors

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Assert.assertEquals
import org.junit.Test

class SensorsTest {

	private val session = mock<ClientSession>()

	@Test
	fun senseCpuTemperatures() {
		session.mockCommandExecution("sensors".toRegex(), """nouveau-pci-0400
Adapter: PCI adapter
GPU core:     +0.60 V  (min =  +0.60 V, max =  +1.20 V)

coretemp-isa-0000
Adapter: ISA adapter
Package id 0:  +35.0°C  (high = +105.0°C, crit = +105.0°C)
Core 0:        +34.0°C  (high = +105.0°C, crit = +105.0°C)
Core 1:        +34.0°C  (high = +105.0°C, crit = +105.0°C)

""".trimIndent())
		val cpuTemperatures = Sensors.senseCpuTemperatures(session)

		assertEquals(2, cpuTemperatures.size)
		assertEquals(0, cpuTemperatures.first().coreId)
		assertEquals(34, cpuTemperatures.first().temperature)
		assertEquals(1, cpuTemperatures[1].coreId)
		assertEquals(34, cpuTemperatures[1].temperature)
	}

	@Test
	fun monitorCpuTemperatures() {
		session.mockProcess(".*sensors.*".toRegex(), """nouveau-pci-0400
Adapter: PCI adapter
GPU core:     +0.60 V  (min =  +0.60 V, max =  +1.20 V)

coretemp-isa-0000
Adapter: ISA adapter
Package id 0:  +40.0°C  (high = +105.0°C, crit = +105.0°C)
Core 0:        +39.0°C  (high = +105.0°C, crit = +105.0°C)
Core 1:        +39.0°C  (high = +105.0°C, crit = +105.0°C)

---end---
nouveau-pci-0400
Adapter: PCI adapter
GPU core:     +0.60 V  (min =  +0.60 V, max =  +1.20 V)

coretemp-isa-0000
Adapter: ISA adapter
Package id 0:  +39.0°C  (high = +105.0°C, crit = +105.0°C)
Core 0:        +38.0°C  (high = +105.0°C, crit = +105.0°C)
Core 1:        +38.0°C  (high = +105.0°C, crit = +105.0°C)

---end---
""")
		val results = mutableListOf<List<CpuTemperatureInfo>>()
		Sensors.monitorCpuTemperatures(session, handler = { results.add(it) })

		assertEquals(2, results.size)
		results.forEach {
			assertEquals(2, it.size)
		}
	}
}