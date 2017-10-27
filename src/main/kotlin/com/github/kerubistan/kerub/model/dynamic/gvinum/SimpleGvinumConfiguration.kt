package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("simple-configuration")
/**
 * This configuration will create the volume on a single gvinum disk
 */
class SimpleGvinumConfiguration(
		val diskId: UUID
) : GvinumConfiguration