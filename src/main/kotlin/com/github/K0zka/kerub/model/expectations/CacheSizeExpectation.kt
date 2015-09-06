package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import java.util.UUID

JsonTypeName("cache-size")
data class CacheSizeExpectation @JsonCreator constructor(
		override val level: ExpectationLevel,
		val minKbytes: Long
                                                        ) : Expectation