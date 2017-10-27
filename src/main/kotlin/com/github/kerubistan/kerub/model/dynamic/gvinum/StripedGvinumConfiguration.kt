package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("striped-config")
class StripedGvinumConfiguration(
		val disks: List<String>
) : GvinumConfiguration