package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(MirroredGvinumConfiguration::class),
		JsonSubTypes.Type(SimpleGvinumConfiguration::class),
		JsonSubTypes.Type(StripedGvinumConfiguration::class)
)

interface GvinumConfiguration : Serializable