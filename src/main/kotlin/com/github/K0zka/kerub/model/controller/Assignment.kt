package com.github.K0zka.kerub.model.controller

import java.util.UUID
import com.github.K0zka.kerub.model.Entity
import org.hibernate.search.annotations.Field
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

JsonCreator
public data class Assignment(
		JsonProperty("id")
		override val id: UUID,
		JsonProperty("controllerId")
		Field
		var controller: String,
		JsonProperty("hostId")
		Field
		var hostId: UUID
                            )
: Entity<UUID>
