package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.CrudDao
import com.github.K0zka.kerub.model.Entity

open class IspnDaoBase<T : Entity<I>, I> : CrudDao<T, I> {
	override fun add(entity: T): I {
		throw UnsupportedOperationException()
	}
	override fun get(id: I): T {
		throw UnsupportedOperationException()
	}
	override fun remove(entity: T) {
		throw UnsupportedOperationException()
	}
	override fun remove(id: I) {
		throw UnsupportedOperationException()
	}
	override fun update(entity: T) {
		throw UnsupportedOperationException()
	}
}