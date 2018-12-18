package com.github.kerubistan.kerub.utils.junix.acpi

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockProcess
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertEquals

class AcpiTest {

	val session = mock<ClientSession>()

	@Test
	fun readBatteryInfoWithFull() {
		session.mockCommandExecution("acpi .*".toRegex(), "Battery 0: Full, 100%\n")
		val batteryInfo = Acpi.readBatteryInfo(session)
		batteryInfo.single().apply {
			assertEquals(batteryId, 0)
			assertEquals(batteryState, BatteryState.Full)
			assertEquals(percent, 100)
		}
	}

	@Test
	fun readBatteryInfoWithCharging() {
		session.mockCommandExecution("acpi .*".toRegex(), "Battery 0: Charging, 92%, 00:00:08 until charged\n")
		val batteryInfo = Acpi.readBatteryInfo(session)
		batteryInfo.single().apply {
			assertEquals(batteryId, 0)
			assertEquals(batteryState, BatteryState.Charging)
			assertEquals(percent, 92)
		}
	}

	@Test
	fun readBatteryInfoWithDischarging() {
		session.mockCommandExecution("acpi .*".toRegex(), "Battery 0: Discharging, 99%, 02:18:01 remaining\n")
		val batteryInfo = Acpi.readBatteryInfo(session)
		batteryInfo.single().apply {
			assertEquals(batteryId, 0)
			assertEquals(batteryState, BatteryState.Discharging)
			assertEquals(percent, 99)
		}
	}

	@Test
	fun monitorBatteryInfo() {
		session.mockProcess(""".*acpi.*""".toRegex(), """Battery 0: Discharging, 64%, 00:58:35 remaining
---end---
Battery 0: Discharging, 64%, 00:58:35 remaining
---end---
Battery 0: Discharging, 64%, 01:15:23 remaining
---end---
Battery 0: Discharging, 64%, 01:14:56 remaining
---end---
Battery 0: Discharging, 64%, 01:45:40 remaining
---end---
Battery 0: Discharging, 64%, 01:44:50 remaining
---end---
""")
		val results = mutableListOf<List<BatteryStatus>>()
		Acpi.monitorBatteryInfo(session, handler = { results.add(it) })
		assertEquals(6, results.size)
		results.forEach {
			assertEquals(64, it.single().percent)
			assertEquals(0, it.single().batteryId)
			assertEquals(BatteryState.Discharging, it.single().batteryState)
		}
	}

}