package com.github.K0zka.kerub.utils.junix.vmstat

import java.io.Serializable
import java.math.BigInteger

data class BsdVmStatEvent (
		val userCpu: Byte,
		val systemCpu: Byte,
		val idleCpu: Byte,
		val freeMem: BigInteger,
		val ioBuffMem: BigInteger,
		val cacheMem: BigInteger,
		val swapMem: BigInteger
) : Serializable