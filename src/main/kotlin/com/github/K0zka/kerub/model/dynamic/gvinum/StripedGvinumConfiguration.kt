package com.github.K0zka.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.dynamic.gvinum.GvinumConfiguration

@JsonTypeName("striped-config")
class StripedGvinumConfiguration (
		val disks : List<String>
) : GvinumConfiguration