package com.github.K0zka.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("mirrored-configuration")
class MirroredGvinumConfiguration(
		val disks: List<String>
) : GvinumConfiguration