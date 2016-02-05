package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.EventDao
import com.github.K0zka.kerub.model.Event
import com.github.K0zka.kerub.services.EventService
import java.util.UUID

public class EventServiceImpl(private val eventDao: EventDao) : EventService {
	override fun getById(id: UUID): Event {
		return assertExist("Event", eventDao[id], id)
	}

	override fun list(): List<Event> {
		return eventDao.list()
	}
}