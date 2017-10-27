package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.dynamic.DynamicEntity

interface DataCollection<S : Entity<*>, D : DynamicEntity> {
	val stat: S
	val dynamic: D?
}