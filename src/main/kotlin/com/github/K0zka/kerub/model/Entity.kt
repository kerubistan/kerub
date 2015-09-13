package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hibernate.search.annotations.DocumentId
import java.io.Serializable

/**
 * Generic entity type.
 * The only sure thing about an entity is that it has got an ID
 */
JsonTypeInfo(use=JsonTypeInfo.Id.NAME , include=JsonTypeInfo.As.PROPERTY, property="@type")
JsonSubTypes(
		JsonSubTypes.Type(Host::class),
		JsonSubTypes.Type(VirtualMachine::class),
		JsonSubTypes.Type(VirtualStorageDevice::class),
		JsonSubTypes.Type(VirtualStorageLink::class),
		JsonSubTypes.Type(Project::class),
		JsonSubTypes.Type(Network::class),
		JsonSubTypes.Type(AuditEntry::class),
		JsonSubTypes.Type(Event::class)
            )
data interface Entity<T> : Serializable {
	DocumentId
	JsonProperty("id")
	val id : T
}