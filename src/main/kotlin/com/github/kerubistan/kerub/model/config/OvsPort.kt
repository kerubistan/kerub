package com.github.kerubistan.kerub.model.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(OvsGrePort::class),
		JsonSubTypes.Type(OvsDataPort::class)
)
interface OvsPort : Serializable {
	val name: String
}