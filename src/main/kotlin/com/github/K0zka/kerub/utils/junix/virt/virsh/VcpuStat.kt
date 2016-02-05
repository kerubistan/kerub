package com.github.K0zka.kerub.utils.junix.virt.virsh

data class VcpuStat(
		val state: VcpuState,
		val time: Long?
)