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
import com.github.K0zka.kerub.utils.avgBy
import com.github.K0zka.kerub.utils.join
import org.infinispan.Cache
import org.infinispan.query.dsl.Query
import java.util.UUID
import kotlin.reflect.KClass

abstract class GenericHistoryDaoImpl<in T : DynamicEntity>(
		private val cache: Cache<UUID, HistoryEntry>
) : HistoryDao<T> {

	companion object {
		internal val appVersion = GenericHistoryDaoImpl::class.java.`package`.implementationVersion

		internal fun changedPropertyNames(changes: List<HistoryEntry>) =
				changes.filterIsInstance(ChangeEvent::class.java).map {
					it.changes.map { it.property }
				}.join().toSet() +
						changes.filterIsInstance(HistorySummary::class.java).map {
							it.changes.map { it.property }
						}.join().toSet()


		internal fun changesOfProperty(propName: String, changes: List<HistoryEntry>) =
				changes.filter {
					when (it) {
						is ChangeEvent -> it.changes.any { it.property == propName }
						is HistorySummary -> it.changes.any { it.property == propName }
						else -> TODO("Not handled HistoryEntry")
					}
				}


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

	internal fun history(from: Long, to: Long, entityId: UUID) =
			// events
			cache.queryBuilder(ChangeEvent::class)
					.having(ChangeEvent::time.name).between(from, to)
					.and().having(ChangeEvent::entityKeyStr.name).eq(entityId.toString()).toBuilder<Query>()
					.orderBy(ChangeEvent::time.name)
					.list<HistoryEntry>() +
					// and summarized events
					cache.queryBuilder(HistorySummary::class)
							.having(HistorySummary::start.name)
							.between(from, to)
							.and()
							.having(HistorySummary::end.name)
							.between(from, to)
							.and()
							.having(HistorySummary::entityKeyStr.name).eq(entityId.toString()).toBuilder<Query>()
							.orderBy(HistorySummary::start.name)
							.list()

	override fun compress(from: Long, to: Long, entityIds: Collection<UUID>) {
		entityIds.forEach { entityId ->
			cache.batch {
				val changes = history(from, to, entityId)

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

	open fun sum(changes: List<HistoryEntry>): List<PropertyChangeSummary> =
			changedPropertyNames(changes).map {

				val propertyChanges = changesOfProperty(it, changes)

				PropertyChangeSummary(
						property = it,
						average = propertyChanges.avgBy { 0 },
						max = propertyChanges.maxBy { 0 },
						min = propertyChanges.minBy { 0 },
						extremes = listOf()
				)
			}


	abstract fun changes(oldEntry: T, newEntry: T): List<PropertyChange>
}