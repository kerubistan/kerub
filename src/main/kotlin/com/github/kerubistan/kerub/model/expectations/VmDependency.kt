package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel
import java.util.UUID

@JsonTypeName("vm-dependency")
data class VmDependency(override val level: ExpectationLevel = ExpectationLevel.DealBreaker, val otherVm: UUID)
	: VirtualMachineExpectation, VirtualMachineReference {
	override val referredVmIds: List<UUID>
		get() = listOf(otherVm)
}