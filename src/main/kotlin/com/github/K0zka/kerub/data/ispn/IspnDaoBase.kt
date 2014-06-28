package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.CrudDao
import com.github.K0zka.kerub.model.Entity
import org.infinispan.Cache

open class IspnDaoBase<T : Entity<I>, I> (protected val cache : Cache<I, T>) : CrudDao<T, I> {
	override fun add(entity: T): I {
		cache.put(entity.id!!, entity)
		return entity.id!!
	}
	override fun get(id: I): T {
		return cache[id]!!
	}
	override fun remove(entity: T) {
		cache.remove(entity.id)
	}
	override fun remove(id: I) {
		cache.remove(id)
	}
	override fun update(entity: T) {
		cache.put(entity.id!!, entity)
	}
}