package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("mirrored-configuration")
class MirroredGvinumConfiguration (
		val disks : List<String>
) : GvinumConfiguration