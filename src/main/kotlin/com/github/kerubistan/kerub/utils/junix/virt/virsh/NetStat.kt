package com.github.kerubistan.kerub.utils.junix.virt.virsh

data class NetStat(
		val name: String,
		val received: NetTrafficStat,
		val sent: NetTrafficStat
)