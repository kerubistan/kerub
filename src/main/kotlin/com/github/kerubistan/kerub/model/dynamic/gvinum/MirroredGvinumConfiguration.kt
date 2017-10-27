package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("mirrored-configuration")
class MirroredGvinumConfiguration(
		val disks: List<String>
) : GvinumConfiguration