package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity

interface CrudDao<T : Entity<I>, I>
: DaoOperations.Read<T, I>, DaoOperations.Add<T, I>, DaoOperations.Remove<T, I>, DaoOperations.Update<T, I> {
	fun update(id: I, change: (T) -> T) {
		update(change(requireNotNull(get(id))))
	}
}