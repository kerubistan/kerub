package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("simple-configuration")
/**
 * This configuration will create the volume on a single gvinum disk
 */
data class SimpleGvinumConfiguration(
		//the single drive the 
		val diskName: String
) : GvinumConfiguration