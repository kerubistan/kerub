package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.SoftwarePackage
import java.util.UUID

JsonTypeName("host-os")
data class HostOperatingSystemExpectation constructor(
		override val id: UUID,
		override val level: ExpectationLevel,
		val os: SoftwarePackage
                                                                    ) : Expectation