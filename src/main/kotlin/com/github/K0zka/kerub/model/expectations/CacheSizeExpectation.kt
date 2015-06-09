package com.github.K0zka.kerub.model.expectations

import com.github.K0zka.kerub.model.Expectation
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID
import com.github.K0zka.kerub.model.ExpectationLevel

JsonTypeName("cache-size")
data class CacheSizeExpectation @JsonCreator constructorconstructor(
		override val id : UUID,
		override val level : ExpectationLevel,
        val minKbytes : Long
                                                ) : Expectation