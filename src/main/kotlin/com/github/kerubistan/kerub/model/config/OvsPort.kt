package com.github.kerubistan.kerub.model.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(OvsGrePort::class),
		JsonSubTypes.Type(OvsDataPort::class)
)
interface OvsPort {
	val name: String
}