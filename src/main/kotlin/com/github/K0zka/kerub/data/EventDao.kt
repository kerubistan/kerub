package com.github.K0zka.kerub.data

import java.util.UUID
import com.github.K0zka.kerub.model.Event

public trait EventDao
: DaoOperations.Read<Event, UUID>, DaoOperations.Add<Event, UUID>, DaoOperations.List<Event, UUID> {
}