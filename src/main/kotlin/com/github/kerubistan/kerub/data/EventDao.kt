package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.Event
import java.util.UUID

interface EventDao
	: DaoOperations.Read<Event, UUID>, DaoOperations.Add<Event, UUID>, DaoOperations.PagedList<Event, UUID>