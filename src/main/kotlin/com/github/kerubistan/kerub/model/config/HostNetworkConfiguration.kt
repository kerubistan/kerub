package com.github.kerubistan.kerub.model.config

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(OvsNetworkConfiguration::class)
)
interface HostNetworkConfiguration {
	val virtualNetworkId: UUID
}