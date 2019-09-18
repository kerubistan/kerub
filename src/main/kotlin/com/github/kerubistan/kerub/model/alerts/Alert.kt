package com.github.kerubistan.kerub.model.alerts

import com.github.kerubistan.kerub.model.Entity
import java.util.UUID

interface Alert : Entity<UUID> {
	override val id: UUID
	val created: Long
	val resolved: Long?
	val open: Boolean
}