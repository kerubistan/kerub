package com.github.K0zka.kerub.model.controller

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.K0zka.kerub.model.Entity
import org.hibernate.search.annotations.Field
import java.util.UUID

public data class Assignment @JsonCreator constructor(
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		@JsonProperty("controllerId")
		@Field
		var controller: String,
		@JsonProperty("hostId")
		@Field
		var hostId: UUID
)
: Entity<UUID>
