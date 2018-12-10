package com.github.kerubistan.kerub.utils.junix.sensors

import java.io.Serializable

data class CpuTemperatureInfo (
		val coreId : Int,
		val temperature : Int,
		val high : Int,
		val critical : Int
) : Serializable