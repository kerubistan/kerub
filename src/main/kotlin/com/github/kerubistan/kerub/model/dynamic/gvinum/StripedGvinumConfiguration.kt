package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("striped-config")
data class StripedGvinumConfiguration(
		val disks: List<String>
) : GvinumConfiguration {
	override val diskNames: Collection<String>
		get() = disks
}