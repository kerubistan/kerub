package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.ExecutionResultDao
import com.github.K0zka.kerub.model.ExecutionResult
import org.infinispan.Cache
import java.util.UUID

class ExecutionResultDaoImpl(private val cache: Cache<UUID, ExecutionResult>)
	: ExecutionResultDao {
	override fun add(entity: ExecutionResult): UUID = entity.let {
		cache.put(it.id, it)
		it.id
	}

	override fun fieldSearch(field: String, value: String, start: Long, limit: Int): List<ExecutionResult> =
			cache.fieldSearch(field, value, start, limit)

	override fun count(): Int = cache.count()

	override fun list(start: Long, limit: Int, sort: String): List<ExecutionResult> =
			cache.search(start = start, limit = limit, sortProp = sort)
}