package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Event
import java.util.UUID

interface EventDao
	: DaoOperations.Read<Event, UUID>, DaoOperations.Add<Event, UUID>, DaoOperations.PagedList<Event, UUID>