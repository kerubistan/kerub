package com.github.K0zka.kerub.data

import java.util.UUID
import com.github.K0zka.kerub.model.Event

public trait EventDao : ListableDao<Event, UUID> {
	fun add(event : Event) : UUID
	fun get(id : UUID) : Event?
}