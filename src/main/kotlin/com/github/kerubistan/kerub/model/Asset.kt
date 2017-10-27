package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hibernate.search.annotations.Field
import java.util.UUID
import kotlin.reflect.KClass

/**
 * An object that can be owned by an account or a project.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(VirtualMachine::class),
		JsonSubTypes.Type(VirtualNetwork::class),
		JsonSubTypes.Type(VirtualStorageDevice::class)
)
interface Asset : Entity<UUID>, Named {
	val owner: AssetOwner?
	fun references(): Map<KClass<out Asset>, List<UUID>>

	val ownerIdStr: String?
		@Field
		@JsonIgnore
		get() = owner?.ownerIdStr

}