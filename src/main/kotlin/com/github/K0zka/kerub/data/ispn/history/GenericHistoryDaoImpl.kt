package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.data.HistoryDao
import com.github.K0zka.kerub.data.ispn.batch
import com.github.K0zka.kerub.data.ispn.list
import com.github.K0zka.kerub.data.ispn.queryBuilder
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import com.github.K0zka.kerub.model.history.ChangeEvent
import com.github.K0zka.kerub.model.history.HistoryEntry
import com.github.K0zka.kerub.model.history.HistorySummary
import com.github.K0zka.kerub.model.history.PropertyChange
import com.github.K0zka.kerub.model.history.PropertyChangeSummary
import org.infinispan.Cache
import java.util.UUID
import kotlin.reflect.KClass

abstract class GenericHistoryDaoImpl<in T : DynamicEntity>(
		private val cache: Cache<UUID, HistoryEntry>
) : HistoryDao<T> {

	companion object {
		internal val appVersion = GenericHistoryDaoImpl::class.java.`package`.implementationVersion
	}

	override fun log(oldEntry: T, newEntry: T) {
		val change = ChangeEvent(
				entityKey = oldEntry.id,
				appVersion = appVersion,
				changes = changes(oldEntry, newEntry)
		)
		cache.putAsync(change.id, change)
	}

	internal fun list(id: UUID, clazz: KClass<out HistoryEntry>): List<HistoryEntry> =
			cache.queryBuilder(clazz).having(HistoryEntry::entityKeyStr.name).eq(id.toString()).list()

	override fun list(id: UUID): List<HistoryEntry> =
			list(id, ChangeEvent::class) + list(id, HistorySummary::class)

	override fun compress(from: Long, to: Long, entityIds: Collection<UUID>) {
		entityIds.forEach { entityId ->
			cache.batch {
				val changes = cache.queryBuilder(ChangeEvent::class)
						.having(ChangeEvent::time.name).between(from, to)
						.and().having(ChangeEvent::entityKeyStr.name).eq(entityId.toString())
						.list<HistoryEntry>()

				if (changes.isNotEmpty()) {
					val summary = HistorySummary(
							appVersion = appVersion,
							changes = sum(changes), //TODO
							entityKey = changes.first().entityKey,
							time = Range<Long>(from, to)
					)

					cache.putAsync(summary.id, summary)

					changes.forEach {
						cache.remove(it.id)
					}
				}
			}
		}
	}

	open fun sum(changes: List<HistoryEntry>): List<PropertyChangeSummary> = listOf()

	abstract fun changes(oldEntry: T, newEntry: T): List<PropertyChange>
}