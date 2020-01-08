package com.github.kerubistan.kerub.model.controller

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Entity
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.util.UUID

@JsonTypeName("assignment")
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
) : Entity<UUID>
