package com.github.K0zka.kerub.utils.junix.virt.virsh

data class NetStat (
		val name : String,
		val received : NetTrafficStat,
		val sent: NetTrafficStat
)