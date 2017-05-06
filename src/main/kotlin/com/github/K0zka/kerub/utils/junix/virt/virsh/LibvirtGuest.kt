package com.github.K0zka.kerub.utils.junix.virt.virsh

import java.io.Serializable

data class LibvirtGuest(
		val osType: String,
		val arch : LibvirtArch
): Serializable