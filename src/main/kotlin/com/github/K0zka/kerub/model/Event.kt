package com.github.K0zka.kerub.model

import java.util.UUID
import com.fasterxml.jackson.annotation.JsonTypeName

JsonTypeName("event")
public data class Event() : Entity<UUID> {
	override var id: UUID? = null
	var userId : UUID? = null
	var message: String? = null
	var date : Long? = null
	var messageType : EventType? = null
}