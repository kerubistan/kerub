package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("simple-configuration")
class SimpleGvinumConfiguration (
		val diskName : String
) : GvinumConfiguration