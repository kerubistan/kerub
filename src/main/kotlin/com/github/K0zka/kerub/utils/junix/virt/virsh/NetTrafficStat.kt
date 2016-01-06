package com.github.K0zka.kerub.utils.junix.virt.virsh

data class NetTrafficStat(
		val bytes: Long,
		val packets: Long,
		val errors: Long,
		val drop: Long
)