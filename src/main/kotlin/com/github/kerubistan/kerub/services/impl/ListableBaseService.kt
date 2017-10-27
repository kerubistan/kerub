package com.github.kerubistan.kerub.services.impl

import com.fasterxml.jackson.annotation.JsonView
import com.github.kerubistan.kerub.data.ListableCrudDao
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.paging.SortResultPage
import com.github.kerubistan.kerub.model.views.Detailed
import com.github.kerubistan.kerub.services.RestOperations
import java.util.UUID

abstract class ListableBaseService<T : Entity<UUID>>(entityType: String)
	: BaseServiceImpl<T>(entityType), RestOperations.List<T> {
	@JsonView(Detailed::class)
	override fun listAll(start: Long, limit: Int, sort: String): SortResultPage<T> =
			SortResultPage(
					start = start,
					count = limit.toLong(), //TODO
					sortBy = sort,
					total = (dao as ListableCrudDao<T, UUID>).count().toLong(),
					result = (dao as ListableCrudDao<T, UUID>).list(start, limit, sort))

}
