package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity

public trait CrudDao<T : Entity<I>, I> : ListableDao<T, I> {
	fun add(entity : T) : I
	fun remove(entity : T)
	fun remove(id : I)
	fun update(entity : T)
	fun get(id : I) : T?
}