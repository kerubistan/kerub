package com.github.kerubistan.kerub.model.dynamic

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.kerubistan.kerub.model.dynamic.gvinum.GvinumStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.lvm.LvmStorageDeviceDynamic
import java.io.Serializable
import java.math.BigInteger
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(SimpleStorageDeviceDynamic::class),
		JsonSubTypes.Type(LvmStorageDeviceDynamic::class),
		JsonSubTypes.Type(GvinumStorageDeviceDynamic::class)
)

interface StorageDeviceDynamic : Serializable {
	val id: UUID
	val freeCapacity: BigInteger
}

