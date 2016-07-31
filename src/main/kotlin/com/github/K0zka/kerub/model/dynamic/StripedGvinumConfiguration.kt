package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("striped-config")
class StripedGvinumConfiguration (
		val disks : List<String>
) : GvinumConfiguration