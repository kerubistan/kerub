package com.github.kerubistan.kerub.utils.junix.virt.virsh

import java.io.Serializable

data class LibvirtArch(
		val name: String,
		val wordsize: Int,
		val emulator: String
) : Serializable