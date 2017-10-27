package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable
import java.math.BigInteger
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(LvmStorageCapability::class),
		JsonSubTypes.Type(FsStorageCapability::class),
		JsonSubTypes.Type(GvinumStorageCapability::class)
)
interface StorageCapability : Serializable {
	val id: UUID
	val size: BigInteger
}