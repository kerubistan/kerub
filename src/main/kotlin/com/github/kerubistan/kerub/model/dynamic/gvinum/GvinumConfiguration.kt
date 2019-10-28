package com.github.kerubistan.kerub.model.dynamic.gvinum

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(MirroredGvinumConfiguration::class),
		JsonSubTypes.Type(SimpleGvinumConfiguration::class),
		JsonSubTypes.Type(StripedGvinumConfiguration::class),
		JsonSubTypes.Type(ConcatenatedGvinumConfiguration::class)
)
interface GvinumConfiguration : Serializable {
	@get:JsonIgnore
	val diskNames : Collection<String>
}