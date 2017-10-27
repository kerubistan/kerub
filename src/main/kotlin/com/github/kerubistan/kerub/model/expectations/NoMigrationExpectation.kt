package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("no-migration")
data class NoMigrationExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.Wish,
		val userTimeoutMinutes: Int
) : VirtualStorageExpectation, VirtualMachineExpectation