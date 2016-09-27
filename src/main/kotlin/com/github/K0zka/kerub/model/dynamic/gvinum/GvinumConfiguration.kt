package com.github.K0zka.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.K0zka.kerub.model.dynamic.gvinum.MirroredGvinumConfiguration
import com.github.K0zka.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.K0zka.kerub.model.dynamic.gvinum.StripedGvinumConfiguration
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(MirroredGvinumConfiguration::class),
		JsonSubTypes.Type(SimpleGvinumConfiguration::class),
		JsonSubTypes.Type(StripedGvinumConfiguration::class),
		JsonSubTypes.Type(ConcatenatedGvinumConfiguration::class)
)
interface GvinumConfiguration : Serializable