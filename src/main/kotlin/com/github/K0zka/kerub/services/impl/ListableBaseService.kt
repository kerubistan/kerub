package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.ResultPage
import java.util.UUID
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.data.ListableCrudDao
import com.github.K0zka.kerub.services.Listable

abstract public class ListableBaseService<T : Entity<UUID>>(dao: ListableCrudDao<T, UUID>, entityType: String)
: BaseServiceImpl<T>(dao, entityType), Listable<T> {
	override fun listAll(start: Long, limit: Long, sort: String): ResultPage<T> {
		return ResultPage(
				start = start,
				count = limit,
				sortBy = sort,
				total = (dao as ListableCrudDao<T, UUID>).count().toLong(),
				result = (dao as ListableCrudDao<T, UUID>).listAll(start, limit, sort))
	}

}