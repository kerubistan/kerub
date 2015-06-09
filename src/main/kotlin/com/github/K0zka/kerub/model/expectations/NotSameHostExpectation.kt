package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import java.util.UUID

JsonTypeName("not-same-host")
data class NotSameHostExpectation @JsonCreator constructor(
		override val id: UUID,
		override val level: ExpectationLevel = ExpectationLevel.Want,
		val otherVmIds: List<UUID>
                                                          ) : Expectation