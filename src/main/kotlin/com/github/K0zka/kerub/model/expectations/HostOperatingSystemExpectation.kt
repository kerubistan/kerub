package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.SoftwarePackage

@JsonTypeName("host-os")
data class HostOperatingSystemExpectation constructor(
		override val level: ExpectationLevel,
		val os: SoftwarePackage
) : VirtualMachineExpectation