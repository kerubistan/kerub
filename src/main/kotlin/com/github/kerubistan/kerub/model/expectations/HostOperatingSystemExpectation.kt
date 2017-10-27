package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.SoftwarePackage

@JsonTypeName("host-os")
data class HostOperatingSystemExpectation constructor(
		override val level: ExpectationLevel,
		val os: SoftwarePackage
) : VirtualMachineExpectation