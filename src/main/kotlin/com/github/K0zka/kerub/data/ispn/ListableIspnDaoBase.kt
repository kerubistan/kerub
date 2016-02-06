package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.DaoOperations
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.Entity
import org.infinispan.Cache
import org.infinispan.query.Search
import org.infinispan.query.dsl.SortOrder

abstract class ListableIspnDaoBase<T : Entity<I>, I>(cache: Cache<I, T>, eventListener: EventListener)
: IspnDaoBase<T, I>(cache, eventListener), DaoOperations.PagedList<T, I> {
	override fun count(): Int {
		return cache.count()
	}

	abstract fun getEntityClass(): Class<T>

	override fun list(start: Long, limit: Long, sort: String): List<T> {
		return Search.getQueryFactory(cache)
				.from(getEntityClass())
				.orderBy(sort, SortOrder.DESC)
				.maxResults(limit.toInt())
				.startOffset(start)
				.build()
				.list<T>() as List<T>
	}
}