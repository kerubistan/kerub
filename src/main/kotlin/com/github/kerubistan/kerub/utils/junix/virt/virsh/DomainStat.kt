package com.github.kerubistan.kerub.utils.junix.virt.virsh

import java.math.BigInteger

data class DomainStat(
		val name: String,
		val vcpuMax: Int,
		val cpuStats: List<VcpuStat>,
		val netStats: List<NetStat>,
		val balloonSize: BigInteger?,
		val balloonMax: BigInteger?
)
