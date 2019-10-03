package com.github.kerubistan.kerub.model.hypervisor

import java.io.Serializable

data class LibvirtArch(
		val name: String,
		val wordsize: Int,
		val emulator: String
) : Serializable