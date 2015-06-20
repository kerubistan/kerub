package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

JsonTypeName("event")
public data class Event(
		override val id: UUID = UUID.randomUUID(),
		var userId: UUID,
		var message: String,
		var date: Long,
		var messageType: EventType
                       )
: Entity<UUID>
