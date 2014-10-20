package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity

public trait ListableDao<T : Entity<I>, I> {
	fun count() : Int
	fun listAll(
			start : Long = 0,
			limit : Long = java.lang.Long.MAX_VALUE,
			sort : String = "id") : List<T>
}