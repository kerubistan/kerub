package com.github.K0zka.kerub.utils.junix.lvm

import java.math.BigInteger
import java.util.UUID

data class VolumeGroup (
		val id : UUID,
		val size : BigInteger,
		val allocatedSize : BigInteger,
		val freeSize : BigInteger,
		val pes : Long,
		val allocatedPes : Long,
		val freePes : Long
)