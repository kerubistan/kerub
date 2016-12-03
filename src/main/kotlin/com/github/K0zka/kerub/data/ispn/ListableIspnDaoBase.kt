package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.DaoOperations
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.Entity
import org.infinispan.Cache
import org.infinispan.query.dsl.SortOrder

abstract class ListableIspnDaoBase<T : Entity<I>, I>(
		cache: Cache<I, T>,
		eventListener: EventListener,
		private val auditManager: AuditManager)
: IspnDaoBase<T, I>(cache, eventListener), DaoOperations.PagedList<T, I> {

	override fun add(entity: T): I {
		auditManager.auditAdd(entity)
		return super.add(entity)
	}

	override fun remove(entity: T) {
		auditManager.auditDelete(entity)
		super.remove(entity)
	}

	override fun remove(id: I) {
		val entity = get(id)
		if (entity != null) {
			remove(entity)
		}
	}

	override fun update(entity: T) {
		val old = get(entity.id)
		if (old != null) {
			auditManager.auditUpdate(old, entity)
		} else {
			auditManager.auditAdd(entity)
		}
		super.update(entity)
	}

	override fun update(id: I, change: (T) -> T) {
		super.update(id) {
			old ->
			val new = change(old)
			auditManager.auditUpdate(old, new)
			new
		}
	}

	override fun count(): Int {
		return cache.count()
	}

	abstract fun getEntityClass(): Class<T>

	override fun list(start: Long, limit: Int, sort: String): List<T> {
		return cache.queryBuilder(getEntityClass().kotlin)
				.maxResults(limit)
				.startOffset(start)
				.orderBy(sort, SortOrder.DESC)
				.list()
	}
}