package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonCreator
import com.github.K0zka.kerub.model.Expectation
import java.util.UUID
import com.github.K0zka.kerub.model.ExpectationLevel

JsonTypeName("ecc-memory")
public class EccMemoryExpectation @JsonCreator constructorconstructor(
		override val id : UUID,
		override val level : ExpectationLevel = ExpectationLevel.DealBreaker
                                               ) : Expectation