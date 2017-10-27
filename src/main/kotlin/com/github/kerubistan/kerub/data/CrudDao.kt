package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.Entity

interface CrudDao<T : Entity<I>, I>
	: DaoOperations.Read<T, I>, DaoOperations.Add<T, I>, DaoOperations.Remove<T, I>, DaoOperations.Update<T, I>, DaoOperations.Listen<I, T> {

	private fun requireGet(id: I): T = requireNotNull(get(id))

	fun update(id: I, retrieve: (I) -> T = this::requireGet, change: (T) -> T) {
		update(change(retrieve(id)))
	}
}