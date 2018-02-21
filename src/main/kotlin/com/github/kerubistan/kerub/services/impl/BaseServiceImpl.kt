package com.github.kerubistan.kerub.services.impl

import com.fasterxml.jackson.annotation.JsonView
import com.github.kerubistan.kerub.data.CrudDao
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.views.Full
import com.github.kerubistan.kerub.services.RestCrud
import java.util.UUID

abstract class BaseServiceImpl<T : Entity<UUID>>(protected val entityType: String)
	: RestCrud<T> {

	protected abstract val dao: CrudDao<T, UUID>

	@JsonView(Full::class)
	override fun getById(id: UUID): T = assertExist(entityType, dao[id], id)

	override fun update(id: UUID, entity: T): T {
		dao.update(entity)
		return entity
	}

	override fun delete(id: UUID) {
		dao.remove(id)
	}

	override fun add(entity: T): T {
		dao.add(entity)
		return entity
	}
}
