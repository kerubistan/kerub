package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import java.util.UUID
import com.fasterxml.jackson.annotation.JsonCreator
import com.github.K0zka.kerub.model.ExpectationLevel

JsonTypeName("cpu-clock-freq")
public class ClockFrequencyExpectation @JsonCreator constructorconstructor(
		override val id: UUID,
		override val level : ExpectationLevel,
		val minimalClockFrequency: Int
                                                    ) : Expectation