package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel
import java.util.UUID

@JsonTypeName("not-same-storage")
data class NotSameStorageExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		val otherDiskId: UUID
) : VirtualStorageExpectation, VirtualStorageDeviceReference {
	override val virtualStorageDeviceReferences: List<UUID>
		@JsonIgnore
		get() = listOf(otherDiskId)
}