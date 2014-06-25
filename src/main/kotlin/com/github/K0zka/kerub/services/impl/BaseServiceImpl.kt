package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.services.RestCrud
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.data.CrudDao

open class BaseServiceImpl<T : Entity<I>, I> (val dao : CrudDao<T, I>) : RestCrud<T, I> {
	override fun getById(id: I): T {
		return dao.get(id)
	}
	override fun update(id: I, entity: T): T {
		dao.update(entity)
		return entity
	}
	override fun delete(id: I) {
		dao.remove(id)
	}
	override fun add(entity: T): T {
		dao.add(entity)
		return entity
	}
}
