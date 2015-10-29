package com.github.K0zka.kerub.services.impl

import com.fasterxml.jackson.annotation.JsonView
import com.github.K0zka.kerub.data.ListableCrudDao
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.paging.ResultPage
import com.github.K0zka.kerub.model.views.Detailed
import com.github.K0zka.kerub.services.RestOperations
import com.github.K0zka.kerub.model.paging.SortResultPage
import java.util.UUID

abstract public class ListableBaseService<T : Entity<UUID>>(dao: ListableCrudDao<T, UUID>, entityType: String)
: BaseServiceImpl<T>(dao, entityType), RestOperations.List<T> {
	@JsonView(Detailed::class)
	override fun listAll(start: Long, limit: Long, sort: String): SortResultPage<T> {
		return SortResultPage(
				start = start,
				count = limit,
				sortBy = sort,
				total = (dao as ListableCrudDao<T, UUID>).count().toLong(),
				result = dao.list(start, limit, sort))
	}

}
