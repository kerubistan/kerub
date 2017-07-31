package com.github.K0zka.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(VirtualStorageLvmAllocation::class),
		JsonSubTypes.Type(VirtualStorageFsAllocation::class),
		JsonSubTypes.Type(VirtualStorageGvinumAllocation::class)
)
interface VirtualStorageAllocation : Serializable {
	val hostId: UUID
	fun getPath(id : UUID) : String
}