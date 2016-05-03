package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.expectations.VirtualStorageExpectation
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import java.math.BigInteger
import java.util.UUID

@JsonTypeName("virtual-storage")
data class VirtualStorageDevice(
		@DocumentId
		@JsonProperty("id")
		override val id: UUID = UUID.randomUUID(),
		@Field
		val size: BigInteger,
		@Field
		val readOnly: Boolean = false,
		@Field
		override
		val expectations: List<VirtualStorageExpectation> = listOf(),
		@Field
		override
		val name: String
)
: Entity<UUID>, Constrained<VirtualStorageExpectation>, Named