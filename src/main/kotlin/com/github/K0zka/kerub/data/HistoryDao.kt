package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.dynamic.DynamicEntity

interface HistoryDao<T : DynamicEntity> {
	fun log(oldEntry : T, newEntry : T)
}