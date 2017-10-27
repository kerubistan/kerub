package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import org.hibernate.search.annotations.Field
import java.io.Serializable
import java.util.UUID

/**
 * Describes the attachment of a virtual disk to a VM
 */
data class VirtualStorageLink(
		@Field
		val virtualStorageId: UUID,
		@Field
		val bus: BusType,
		@Field
		override
		val expectations: List<Expectation> = listOf(),
		@Field
		val device: DeviceType = DeviceType.disk,
		@Field
		val readOnly: Boolean = false
) : Serializable, Constrained<Expectation>