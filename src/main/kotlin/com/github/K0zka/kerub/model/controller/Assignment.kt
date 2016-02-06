package com.github.K0zka.kerub.model.controller

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.K0zka.kerub.model.Entity
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.util.UUID

data class Assignment @JsonCreator constructor(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		@JsonProperty("controllerId")
		@Field
		val controller: String,
		@JsonProperty("type")
		@Field
		val type: AssignmentType,
		@JsonProperty("entityId")
		@Field
		val entityId: UUID
)
: Entity<UUID>
