package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.search.annotations.DocumentId
import java.util.UUID

data class Network(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID
)
	: Entity<UUID>
