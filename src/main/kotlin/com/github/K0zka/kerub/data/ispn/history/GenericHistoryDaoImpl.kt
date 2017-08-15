package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.data.HistoryDao
import com.github.K0zka.kerub.data.ispn.list
import com.github.K0zka.kerub.data.ispn.queryBuilder
import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import com.github.K0zka.kerub.model.history.ChangeEvent
import com.github.K0zka.kerub.model.history.HistoryEntry
import com.github.K0zka.kerub.model.history.HistorySummary
import com.github.K0zka.kerub.model.history.PropertyChange
import org.infinispan.Cache
import java.util.UUID
import kotlin.reflect.KClass

abstract class GenericHistoryDaoImpl<in T : DynamicEntity>(
		private val cache: Cache<UUID, HistoryEntry>
) : HistoryDao<T> {
	override fun log(oldEntry: T, newEntry: T) {
		val change = ChangeEvent(
				entityKey = oldEntry.id,
				appVersion = GenericHistoryDaoImpl::class.java.`package`.implementationVersion,
				changes = changes(oldEntry, newEntry)
		)
		cache.putAsync(change.id, change)
	}

	internal fun list(id: UUID, clazz: KClass<out HistoryEntry>): List<HistoryEntry> =
			cache.queryBuilder(clazz).having(HistoryEntry::entityKeyStr.name).eq(id.toString()).list()

	override fun list(id: UUID): List<HistoryEntry> =
			list(id, ChangeEvent::class) + list(id, HistorySummary::class)

	abstract fun changes(oldEntry: T, newEntry: T): List<PropertyChange>
}