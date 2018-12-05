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
/**
 * A physical storage capability of a server. like a filesystem or a volume group, not the raw disk.
 */
interface StorageCapability : Serializable {
	val id: UUID
	val size: BigInteger
	val performanceInfo: Any?
}