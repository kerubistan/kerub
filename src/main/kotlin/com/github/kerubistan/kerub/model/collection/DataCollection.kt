package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.dynamic.DynamicEntity
import java.io.Serializable

interface DataCollection<I, S : Entity<I>, D : DynamicEntity> : Serializable, Entity<I> {
	val stat: S
	val dynamic: D?
	override val id: I
		get() = stat.id
}