package com.github.K0zka.kerub.utils.junix.virt.virsh

import sun.awt.OSInfo

data class LibvirtGuest(
		val osType: String,
		val arch : LibvirtArch
)