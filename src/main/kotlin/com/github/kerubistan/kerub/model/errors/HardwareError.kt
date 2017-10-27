package com.github.kerubistan.kerub.model.errors

import com.github.kerubistan.kerub.model.Entity
import java.util.UUID

data class HardwareError(
		override val id: UUID = UUID.randomUUID()
) : Entity<UUID>