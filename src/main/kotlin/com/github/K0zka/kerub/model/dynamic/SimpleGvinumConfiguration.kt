package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("simple-configuration")
class SimpleGvinumConfiguration (
		val diskId : UUID
) : GvinumConfiguration