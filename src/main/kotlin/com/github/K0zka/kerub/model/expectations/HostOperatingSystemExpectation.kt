package com.github.K0zka.kerub.model.expectations

import com.github.K0zka.kerub.model.Expectation
import com.fasterxml.jackson.annotation.JsonCreator
import java.util.UUID
import com.github.K0zka.kerub.utils.SoftwarePackage
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel

JsonTypeName("host-os")
public class HostOperatingSystemExpectation @JsonCreator constructorconstructor(
		override val id : UUID,
		override val level : ExpectationLevel,
        val os : SoftwarePackage
                                                          ): Expectation