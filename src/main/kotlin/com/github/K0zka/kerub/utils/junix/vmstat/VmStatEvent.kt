package com.github.K0zka.kerub.utils.junix.vmstat

import java.io.Serializable
import java.math.BigInteger

data class VmStatEvent(
		val userCpu: Byte,
		val systemCpu: Byte,
		val idleCpu: Byte,
		val iowaitCpu: Byte,
		val swap: IoStatistic,
		val block: IoStatistic,
		val freeMem: BigInteger,
		val ioBuffMem: BigInteger,
		val cacheMem: BigInteger,
		val swapMem: BigInteger
) : Serializable