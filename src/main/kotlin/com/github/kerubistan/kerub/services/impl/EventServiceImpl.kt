package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.EventDao
import com.github.kerubistan.kerub.model.Event
import com.github.kerubistan.kerub.services.EventService
import java.util.UUID

class EventServiceImpl(private val eventDao: EventDao) : EventService {
	override fun getById(id: UUID): Event {
		return assertExist("Event", eventDao[id], id)
	}

	override fun list(): List<Event> {
		return eventDao.list()
	}
}