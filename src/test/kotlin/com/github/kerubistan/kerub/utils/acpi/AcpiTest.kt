package com.github.kerubistan.kerub.utils.acpi

import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
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

}