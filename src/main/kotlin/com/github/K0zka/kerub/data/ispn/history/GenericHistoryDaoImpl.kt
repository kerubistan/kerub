package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.data.HistoryDao
import com.github.K0zka.kerub.data.ispn.batch
import com.github.K0zka.kerub.data.ispn.list
import com.github.K0zka.kerub.data.ispn.queryBuilder
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import com.github.K0zka.kerub.model.history.ChangeEvent
import com.github.K0zka.kerub.model.history.DataPropertyChangeSummary
import com.github.K0zka.kerub.model.history.HistoryEntry
import com.github.K0zka.kerub.model.history.HistorySummary
import com.github.K0zka.kerub.model.history.NumericPropertyChangeSummary
import com.github.K0zka.kerub.model.history.PropertyChange
import com.github.K0zka.kerub.model.history.PropertyChangeSummary
import com.github.K0zka.kerub.utils.bd
import com.github.K0zka.kerub.utils.decimalAvgBy
import com.github.K0zka.kerub.utils.equalsAnyOf
import com.github.K0zka.kerub.utils.join
import com.github.K0zka.kerub.utils.subLists
import org.infinispan.Cache
import org.infinispan.query.dsl.Query
import java.math.BigDecimal
import java.util.UUID
import kotlin.reflect.KClass

abstract class GenericHistoryDaoImpl<in T : DynamicEntity>(
		private val cache: Cache<UUID, HistoryEntry>
) : HistoryDao<T> {

	companion object {

		internal val minimumExtreme = 10

		internal val appVersion = GenericHistoryDaoImpl::class.java.`package`.implementationVersion

		internal fun getPropertyType(property: String, ofClazz: KClass<out Any>) =
				ofClazz.java.declaredFields.firstOrNull { it.name == property }?.type

		internal fun isNumber(clazz : Class<*>) =
				if(clazz.isPrimitive) {
					clazz.name.equalsAnyOf("double","float","byte","char","int", "long")
				} else {
					Number::class.java.isAssignableFrom(clazz)
				}

		internal fun isData(clazz : Class<*>): Boolean = clazz.kotlin.isData

		internal fun isList(clazz: Class<*>): Boolean =
				clazz.isAssignableFrom(List::class.java)


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


		private val nevValueAsNumber: (Pair<Long, PropertyChange>) -> BigDecimal = {
			bd(it.second.newValue) ?: BigDecimal.ZERO
		}

		/**
		 * TODO
		 *
		 * Some typical workload as a reminder for myself:
		 *  - batch style: occurs regularly a static number of times a day
		 *  - ad-hoc style:
		 *
		 *  An extreme is:
		 *  A period of time when the values are higher than the average
		 *
		 *  Maximum number of extremes:
		 *  There should be a maximum number of extremes, if there are more extremes than that,
		 *  then maybe we should call it the normal behavior of the application.
		 */
		internal fun detectExtremes(changes: List<Pair<Long, PropertyChange>>) : List<List<PropertyChange>> {
			val sortedChanges = changes.sortedBy(nevValueAsNumber)

			val totalAvg = changes.decimalAvgBy(nevValueAsNumber)
			val median = sortedChanges.minBy {
				(nevValueAsNumber(it) - totalAvg).abs()
			}
			val medianPosition = sortedChanges.indexOf(median)

			val upperHalf = sortedChanges.subList(medianPosition, sortedChanges.size)
			val upperAvg = upperHalf.decimalAvgBy(nevValueAsNumber)


			return changes.subLists(minimumExtreme) {
				nevValueAsNumber(it) > upperAvg
			}.map { it.map { it.second } }
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
							time = Range(from, to)
					)

					cache.putAsync(summary.id, summary)

					changes.forEach {
						cache.remove(it.id)
					}
				}
			}
		}
	}

	internal abstract val dynClass : KClass<out DynamicEntity>

	open fun sum(changes: List<HistoryEntry>): List<PropertyChangeSummary> =
			changedPropertyNames(changes).map {

				changedProperty ->
				val propertyChanges = changesOfProperty(changedProperty, changes)

				val propertyType = getPropertyType(changedProperty, dynClass)
				if(propertyType == null) {
					TODO()
				} else if(isNumber(propertyType)) {
					fun genericSelector(summarySelector : (HistorySummary) -> BigDecimal) : (HistoryEntry) -> BigDecimal =
							{
								when (it) {
									is HistorySummary ->
										summarySelector(it)
									is ChangeEvent ->
										bd(it.changes.single { it.property == changedProperty }.newValue)
												?: BigDecimal.ZERO
									else -> TODO()
								}
							}

					val maxSelector: (HistoryEntry) -> BigDecimal
							= genericSelector {
						bd((it.changes.single { it.property == changedProperty } as NumericPropertyChangeSummary).max)
								?: BigDecimal.ZERO
					}
					val minSelector: (HistoryEntry) -> BigDecimal
							= genericSelector {
						bd((it.changes.single { it.property == changedProperty } as NumericPropertyChangeSummary).min)
								?: BigDecimal.ZERO
					}

					NumericPropertyChangeSummary(
							property = changedProperty,
							average = propertyChanges.decimalAvgBy { BigDecimal.ZERO /*TODO*/},
							max = requireNotNull(propertyChanges.map(maxSelector).max()), //there is at least one element, therefore there must be a maximum
							min = requireNotNull(propertyChanges.map(minSelector).min()), //and same for minimums
							extremes = listOf()
					)

				} else if (isData(propertyType)){
					//TODO
					DataPropertyChangeSummary(
							property = changedProperty,
							changes = mapOf()
					)

				} else if(isList(propertyType)) {
					//TODO
					DataPropertyChangeSummary(
							property = changedProperty,
							changes = mapOf()
					)
				} else {
					//TODO
					DataPropertyChangeSummary(
							property = changedProperty,
							changes = mapOf()
					)

				}

			}


	abstract fun changes(oldEntry: T, newEntry: T): List<PropertyChange>
}