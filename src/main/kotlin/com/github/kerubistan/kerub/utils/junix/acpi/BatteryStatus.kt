package com.github.kerubistan.kerub.utils.junix.acpi

data class BatteryStatus(
		val batteryId : Int,
		val batteryState: BatteryState,
		val percent : Int
)