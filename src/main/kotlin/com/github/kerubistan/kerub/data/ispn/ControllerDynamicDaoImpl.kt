package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.dynamic.ControllerDynamicDao
import com.github.kerubistan.kerub.model.dynamic.ControllerDynamic
import com.github.kerubistan.kerub.utils.byId
import org.infinispan.Cache

class ControllerDynamicDaoImpl(val cache: Cache<String, ControllerDynamic>) : ControllerDynamicDao {
	override fun get(ids: Collection<String>): List<ControllerDynamic> =
			cache.advancedCache.getAll(ids.toHashSet()).values.toList()

	override fun listAll(): List<ControllerDynamic> {
		return cache.map { it.value }
	}

	override fun add(entity: ControllerDynamic): String {
		cache.put(entity.id, entity)
		return entity.controllerId
	}

	override fun get(id: String): ControllerDynamic? = cache[id]

	override fun addAll(entities: Collection<ControllerDynamic>) {
		cache.putAll(entities.byId())
	}
}