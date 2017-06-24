package com.github.K0zka.kerub.data.dynamic

import com.github.K0zka.kerub.data.CrudDao
import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import java.util.UUID

interface DynamicDao<T : DynamicEntity> : CrudDao<T, UUID>

inline fun <T : DynamicEntity> DynamicDao<T>.doWithDyn(
		id: UUID,
		crossinline blank: () -> T,
		crossinline action: (T) -> T) {
	this.update(id, retrieve = { this[id] ?: blank() }, change = { action(it) })
}

