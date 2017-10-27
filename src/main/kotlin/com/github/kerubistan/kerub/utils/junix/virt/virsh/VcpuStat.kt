package com.github.kerubistan.kerub.utils.junix.virt.virsh

data class VcpuStat(
		val state: VcpuState,
		val time: Long?
)