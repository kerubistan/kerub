package com.github.K0zka.kerub.model

import java.util.UUID
import com.fasterxml.jackson.annotation.JsonTypeName

JsonTypeName("event")
public data class Event(
		override val id: UUID = UUID.randomUUID(),
		var userId: UUID,
		var message: String,
		var date: Long,
		var messageType: EventType
                       )
: Entity<UUID>
