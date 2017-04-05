package com.github.K0zka.kerub.model.errors

import com.github.K0zka.kerub.model.Entity
import java.util.UUID

data class HardwareError(
		override val id: UUID = UUID.randomUUID()
) : Entity<UUID>