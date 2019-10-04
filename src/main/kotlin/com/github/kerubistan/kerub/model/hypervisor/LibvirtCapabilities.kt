package com.github.kerubistan.kerub.model.hypervisor

import com.fasterxml.jackson.annotation.JsonTypeName
import java.io.Serializable

@JsonTypeName("libvirt-capabilities")
data class LibvirtCapabilities(
		val guests: List<LibvirtGuest> = listOf()
) : Serializable, HypervisorCapabilities