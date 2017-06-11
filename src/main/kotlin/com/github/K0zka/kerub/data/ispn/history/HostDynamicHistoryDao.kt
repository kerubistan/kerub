package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.history.HistoryEntry
import com.github.K0zka.kerub.model.history.PropertyChange
import org.infinispan.Cache
import java.util.UUID

class HostDynamicHistoryDao(cache: Cache<UUID, HistoryEntry>) : GenericHistoryDaoImpl<HostDynamic>(cache) {
	override fun changes(oldEntry: HostDynamic, newEntry: HostDynamic): List<PropertyChange> = diff(oldEntry, newEntry)
}