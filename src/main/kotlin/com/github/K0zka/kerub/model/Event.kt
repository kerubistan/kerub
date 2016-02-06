package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.hibernate.search.annotations.DocumentId
import java.util.UUID

@JsonTypeName("event")
data class Event(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		var userId: UUID,
		var message: String,
		var date: Long,
		var messageType: EventType
)
: Entity<UUID>
