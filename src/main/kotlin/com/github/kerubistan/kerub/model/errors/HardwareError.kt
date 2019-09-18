package com.github.kerubistan.kerub.model.errors

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Entity
import java.util.UUID

@JsonTypeName("hardware-error")
data class HardwareError(
		override val id: UUID = UUID.randomUUID()
) : Entity<UUID>