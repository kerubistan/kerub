package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.data.HistoryDao
import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import com.github.K0zka.kerub.model.history.HistoryEntry
import com.github.K0zka.kerub.model.history.PropertyChange
import org.infinispan.Cache
import java.util.UUID

abstract class GenericHistoryDaoImpl<T : DynamicEntity>(private val cache : Cache<UUID, HistoryEntry>) : HistoryDao<T> {
	override fun log(oldEntry: T, newEntry: T) {
		val change = HistoryEntry(
				entityKey = oldEntry.id,
				appVersion = GenericHistoryDaoImpl::class.java.`package`.implementationVersion,
				changes = changes(oldEntry, newEntry)
		)
		cache.putAsync(change.id, change)
	}
	abstract fun changes(oldEntry: T, newEntry: T) : List<PropertyChange>
}