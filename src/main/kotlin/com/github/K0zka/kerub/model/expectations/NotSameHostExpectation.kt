package com.github.K0zka.kerub.model.expectations

import com.github.K0zka.kerub.model.Expectation
import com.fasterxml.jackson.annotation.JsonCreator
import java.util.UUID
import com.github.K0zka.kerub.model.ExpectationLevel
import com.fasterxml.jackson.annotation.JsonTypeName

JsonTypeName("not-same-host")
data class NotSameHostExpectation @JsonCreator constructorconstructor(
		override val id : UUID,
		override val level : ExpectationLevel = ExpectationLevel.Want,
        val otherVmIds : List<UUID>
                                                  ) : Expectation