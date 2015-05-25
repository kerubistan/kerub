package com.github.K0zka.kerub.model

import com.github.K0zka.kerub.model.io.BusType
import java.util.UUID

data class StorageDevice(
		override val id: UUID,
		val size: Long,
		val bus : BusType,
		val readOnly :Boolean = false,
		val expectations: List<Expectation> = serializableListOf()
               )
: Entity<UUID>
