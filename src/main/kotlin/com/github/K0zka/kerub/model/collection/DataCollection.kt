package com.github.K0zka.kerub.model.collection

import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.dynamic.DynamicEntity

interface DataCollection<S : Entity<*>, D : DynamicEntity> {
	val stat : S
	val dynamic : D?
}