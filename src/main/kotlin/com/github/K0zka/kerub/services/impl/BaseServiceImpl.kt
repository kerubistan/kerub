package com.github.K0zka.kerub.services.impl

import com.fasterxml.jackson.annotation.JsonView
import com.github.K0zka.kerub.data.CrudDao
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.views.Full
import com.github.K0zka.kerub.services.RestCrud
import java.util.UUID

abstract class BaseServiceImpl<T : Entity<UUID>>(protected val entityType: String)
	: RestCrud<T> {

	abstract protected val dao: CrudDao<T, UUID>

	@JsonView(Full::class)
	override fun getById(id: UUID): T {
		return assertExist(entityType, dao[id], id)
	}

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
