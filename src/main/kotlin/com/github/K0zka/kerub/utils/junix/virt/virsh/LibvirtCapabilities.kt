package com.github.K0zka.kerub.utils.junix.virt.virsh

import java.io.Serializable

data class LibvirtCapabilities (
		var guests : List<LibvirtGuest> = listOf()
) : Serializable