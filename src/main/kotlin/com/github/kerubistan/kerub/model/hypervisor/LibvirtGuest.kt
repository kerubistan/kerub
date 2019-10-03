package com.github.kerubistan.kerub.model.hypervisor

import java.io.Serializable

data class LibvirtGuest(
		val osType: String,
		val arch: LibvirtArch
) : Serializable