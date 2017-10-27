package com.github.kerubistan.kerub.model.dynamic

import com.github.kerubistan.kerub.model.Entity
import java.util.UUID

interface DynamicEntity : Entity<UUID> {
	val lastUpdated: Long

}