package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel
import java.util.UUID

@JsonTypeName("vm-dependency")
data class VmDependency(override val level: ExpectationLevel, val otherVm: List<UUID>)
: VirtualMachineExpectation, VirtualMachineReference {
	override val referredVmIds: List<UUID>
		get() = listOf()
}