package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventDao
import com.github.K0zka.kerub.model.Event
import org.infinispan.AdvancedCache
import org.infinispan.query.Search
import org.infinispan.query.dsl.SortOrder
import java.util.UUID

class EventDaoImpl(val cache: AdvancedCache<UUID, Event>) : EventDao {

	override fun get(ids: Collection<UUID>): List<Event>
			= cache.advancedCache.getAll(ids.toHashSet()).values.toList()

	override fun count(): Int {
		return cache.count()
	}

	override fun add(entity: Event): UUID {
		val id = UUID.randomUUID()
		cache.putAsync(id, entity.copy(id = id))
		return id
	}

	override fun get(id: UUID): Event? {
		return cache.get(id)
	}

	override fun list(start: Long, limit: Long, sort: String): List<Event> {
		return Search.getQueryFactory(cache)
				.from(Event::class.java)
				.orderBy(sort, SortOrder.DESC)
				.maxResults(limit.toInt())
				.startOffset(start)
				.build()
				.list<Event>() as List<Event>
	}
}