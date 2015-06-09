package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import java.util.UUID

public class NoMigrationExpectation @JsonCreator constructor(
		override val id: UUID,
		override val level: ExpectationLevel = ExpectationLevel.Hint,
		val userTimeoutMinutes: Int
                                                            ) : Expectation