package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.io.BusType
import org.hibernate.search.annotations.Field
import java.io.Serializable
import java.util.UUID

/**
 * Describes the attachment of a virtual disk to a VM
 */
data class VirtualStorageLink(
		Field
		val virtualStorageId: UUID,
		Field
		val bus: BusType,
		Field
		override
		val expectations: List<Expectation> = listOf()
                             ) : Serializable, Constrained<Expectation>