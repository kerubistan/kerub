package com.github.kerubistan.kerub.model.services

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonSubTypes.Type as type

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		type(IscsiService::class)
)
interface HostService : Serializable