package com.github.K0zka.kerub.utils.junix.procfs

import java.io.Serializable

data class CpuStat (
		val user : Int,
		val system : Int,
		val idle : Int
) : Serializable