package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.kerubistan.kerub.model.StorageCapability
import java.io.Serializable
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KClass

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(VirtualStorageLvmAllocation::class),
		JsonSubTypes.Type(VirtualStorageFsAllocation::class),
		JsonSubTypes.Type(VirtualStorageGvinumAllocation::class)
)
interface VirtualStorageAllocation : Serializable {
	val hostId: UUID
	val capabilityId : UUID
	val actualSize: BigInteger
	fun requires() : KClass<out StorageCapability>
	fun getPath(id : UUID) : String
	// I would not need this with data class hierarchy
	// coming in kotlin 1.2
	fun resize(newSize : BigInteger) : VirtualStorageAllocation
}