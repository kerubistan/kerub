package com.github.K0zka.kerub.model

import com.github.K0zka.kerub.model.io.BusType
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.util.UUID

data class VirtualStorageDevice(
		DocumentId
		override val id: UUID,
		Field
		val size: Long,
		Field
		val readOnly :Boolean = false,
		Field
		val expectations: List<Expectation> = listOf()
               )
: Entity<UUID>
