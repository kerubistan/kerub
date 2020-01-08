package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.hibernate.search.annotations.DocumentId
import java.util.UUID

@JsonTypeName("event")
data class Event(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		val userId: UUID,
		val message: String,
		val date: Long,
		val messageType: EventType
) : Entity<UUID>
