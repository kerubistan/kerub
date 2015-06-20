package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Event
import java.util.UUID

public interface EventDao
: DaoOperations.Read<Event, UUID>, DaoOperations.Add<Event, UUID>, DaoOperations.List<Event, UUID> {
}