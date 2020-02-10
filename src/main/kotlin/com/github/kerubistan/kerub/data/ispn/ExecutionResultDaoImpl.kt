package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.ExecutionResultDao
import com.github.kerubistan.kerub.model.ExecutionResult
import com.github.kerubistan.kerub.utils.byId
import com.github.kerubistan.kerub.utils.getLogger
import org.infinispan.Cache
import org.infinispan.commons.CacheException
import java.util.UUID

class ExecutionResultDaoImpl(private val cache: Cache<UUID, ExecutionResult>) : ExecutionResultDao {

	companion object {
		private val logger = getLogger()
	}

	override fun addAll(entities: Collection<ExecutionResult>) {
		cache.putAll(entities.byId())
	}

	override fun add(entity: ExecutionResult): UUID = entity.let {
		try {
			cache[it.id] = it
		} catch (ce: CacheException) {
			logger.error("Could not persist execution result $it ", ce)
		}
		it.id
	}

	override fun fieldSearch(field: String, value: String, start: Long, limit: Int): List<ExecutionResult> =
			cache.fieldSearch(field, value, start, limit)

	override fun count(): Int = cache.count()

	override fun list(start: Long, limit: Int, sort: String): List<ExecutionResult> =
			cache.search(start = start, limit = limit, sortProp = sort)
}