package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.ResultPage
import java.util.UUID
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.data.ListableCrudDao
import com.github.K0zka.kerub.services.RestOperations

abstract public class ListableBaseService<T : Entity<UUID>>(dao: ListableCrudDao<T, UUID>, entityType: String)
: BaseServiceImpl<T>(dao, entityType), RestOperations.List<T> {
	override fun listAll(start: Long, limit: Long, sort: String): ResultPage<T> {
		return ResultPage(
				start = start,
				count = limit,
				sortBy = sort,
				total = (dao as ListableCrudDao<T, UUID>).count().toLong(),
				result = (dao as ListableCrudDao<T, UUID>).list(start, limit, sort))
	}

}