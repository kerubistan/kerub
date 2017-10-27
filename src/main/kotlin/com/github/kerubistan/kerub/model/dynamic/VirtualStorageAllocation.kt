package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable
import java.math.BigInteger
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(VirtualStorageLvmAllocation::class),
		JsonSubTypes.Type(VirtualStorageFsAllocation::class),
		JsonSubTypes.Type(VirtualStorageGvinumAllocation::class)
)
interface VirtualStorageAllocation : Serializable {
	val hostId: UUID
	val actualSize: BigInteger
	fun getPath(id : UUID) : String
	// I would not need this with data class hierarchy
	// coming in kotlin 1.2
	fun resize(newSize : BigInteger) : VirtualStorageAllocation
}