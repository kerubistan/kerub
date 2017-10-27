package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel
import java.util.UUID

@JsonTypeName("not-same-host")
data class NotSameHostExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.Want,
		val otherVmId: UUID
) : VirtualMachineExpectation, VirtualMachineReference {
	override val referredVmIds: List<UUID>
		@JsonIgnore
		get() = listOf(otherVmId)
}