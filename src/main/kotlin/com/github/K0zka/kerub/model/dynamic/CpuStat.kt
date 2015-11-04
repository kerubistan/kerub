package com.github.K0zka.kerub.model.dynamic

data class CpuStat (
		val cpuNr : Int,
		val user : Float,
		val system : Float,
		val ioWait : Float,
		val idle : Float
)