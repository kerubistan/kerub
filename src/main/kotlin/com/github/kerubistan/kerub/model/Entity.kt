package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import java.io.Serializable

/**
 * Generic entity type.
 * The only sure thing about an entity is that it has got an ID
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(Account::class),
		JsonSubTypes.Type(Host::class),
		JsonSubTypes.Type(HostDynamic::class),
		JsonSubTypes.Type(VirtualMachine::class),
		JsonSubTypes.Type(VirtualNetwork::class),
		JsonSubTypes.Type(VirtualStorageDevice::class),
		JsonSubTypes.Type(Project::class),
		JsonSubTypes.Type(Network::class),
		JsonSubTypes.Type(AddEntry::class),
		JsonSubTypes.Type(DeleteEntry::class),
		JsonSubTypes.Type(UpdateEntry::class),
		JsonSubTypes.Type(Event::class)
)
interface Entity<T> : Serializable {
	//	@DocumentId
	//	@JsonProperty("id")
	val id: T
}