package com.github.kerubistan.kerub.utils.junix.virt.virsh

import java.io.Serializable

data class LibvirtGuest(
		val osType: String,
		val arch: LibvirtArch
) : Serializable