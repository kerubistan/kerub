package com.github.K0zka.kerub.data

import com.github.K0zka.kerub.model.Entity

public trait ListableDao<T : Entity<I>, I> {
	fun listAll() : List<T>
}