package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("mirrored-configuration")
data class MirroredGvinumConfiguration(
		val disks: List<String>
) : GvinumConfiguration {
	override val diskNames: Collection<String> get() = disks
}