package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.RestCrud
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.data.CrudDao
import java.util.UUID
import com.github.K0zka.kerub.services.Listable

open class BaseServiceImpl<T : Entity<UUID>> (protected val dao : CrudDao<T, UUID>, val entityType : String)
	: RestCrud<T>, Listable<T> {
	override fun listAll(start: Long, limit: Long, sort: String): List<T> {
		return dao.listAll(start, limit, sort)
	}

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
		if(entity.id == null) {
			entity.id = UUID.randomUUID()
		}
		dao.add(entity)
		return entity
	}
}
