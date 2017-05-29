package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventDao
import com.github.K0zka.kerub.model.Event
import org.infinispan.AdvancedCache
import org.infinispan.query.Search
import org.infinispan.query.dsl.SortOrder
import java.util.UUID

class EventDaoImpl(private val cache: AdvancedCache<UUID, Event>) : EventDao {

	override fun get(ids: Collection<UUID>): List<Event>
			= cache.advancedCache.getAll(ids.toHashSet()).values.toList()

	override fun count() = cache.count()

	override fun add(entity: Event): UUID =
			UUID.randomUUID().let {
				cache.putAsync(it, entity.copy(id = it))
				it
			}

	override fun get(id: UUID): Event? = cache[id]

	override fun list(start: Long, limit: Int, sort: String): List<Event> =
			Search.getQueryFactory(cache)
					.from(Event::class.java)
					.orderBy(sort, SortOrder.DESC)
					.maxResults(limit)
					.startOffset(start)
					.list()
}