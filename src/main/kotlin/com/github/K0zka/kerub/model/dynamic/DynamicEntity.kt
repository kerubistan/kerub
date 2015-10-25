package com.github.K0zka.kerub.model.dynamic

import com.github.K0zka.kerub.model.Entity
import java.util.UUID

interface DynamicEntity : Entity<UUID> {
	val lastUpdated: Long

}