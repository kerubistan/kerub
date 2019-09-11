package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable
import java.math.BigInteger
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(SimpleStorageDeviceDynamic::class),
		JsonSubTypes.Type(CompositeStorageDeviceDynamic::class)
)

interface StorageDeviceDynamic : Serializable {
	val id: UUID
	val freeCapacity: BigInteger

	fun withFreeCapacity(freeCapacity : BigInteger) : StorageDeviceDynamic
}

