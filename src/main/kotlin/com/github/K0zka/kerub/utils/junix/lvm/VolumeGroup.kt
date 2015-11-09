package com.github.K0zka.kerub.utils.junix.lvm

import java.math.BigInteger
import java.util.UUID

data class VolumeGroup (
		val id : String,
		val name : String,
		val size : BigInteger,
		val freeSize : BigInteger,
		val pes : Long,
		val freePes : Long
)