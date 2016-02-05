package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel

@JsonTypeName("no-migration")
data class NoMigrationExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.Wish,
		val userTimeoutMinutes: Int
) : VirtualStorageExpectation, VirtualMachineExpectation