package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.io.BusType
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.util.UUID

JsonTypeName("vstorage")
data class VirtualStorageDevice(
		DocumentId
		override val id: UUID = UUID.randomUUID(),
		Field
		val size: Long,
		Field
		val readOnly :Boolean = false,
		Field
		override
		val expectations: List<Expectation> = listOf()
               )
: Entity<UUID>, Constrained<Expectation>
