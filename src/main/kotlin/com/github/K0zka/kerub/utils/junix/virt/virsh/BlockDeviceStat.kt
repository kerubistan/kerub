package com.github.K0zka.kerub.utils.junix.virt.virsh

import java.math.BigInteger

data class BlockDeviceStat(
		val name: String,
		val readStat: IoStat,
		val writeStat: IoStat,
		val allocation: BigInteger,
		val capacity: BigInteger,
		val physical: BigInteger
)