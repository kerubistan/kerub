package com.github.K0zka.kerub.model.hardware

data class MemoryInformation (
		val sizeMb: Int,
		val type: String,
		val formFactor: String,
		val locator: String,
		val bankLocator: String,
		val speedMhz: Int,
		val manufacturer: String,
		val serialNumber: String,
		val partNumber: String,
		val configuredSpeedMhz: Int)