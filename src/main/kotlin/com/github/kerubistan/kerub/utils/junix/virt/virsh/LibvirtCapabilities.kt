package com.github.kerubistan.kerub.utils.junix.virt.virsh

import java.io.Serializable

data class LibvirtCapabilities(
		var guests: List<LibvirtGuest> = listOf()
) : Serializable