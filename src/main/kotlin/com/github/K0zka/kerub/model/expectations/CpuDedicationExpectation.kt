package com.github.K0zka.kerub.model.expectations

import com.github.K0zka.kerub.model.Expectation
import com.fasterxml.jackson.annotation.JsonCreator
import java.util.UUID
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel

JsonTypeName("cpu-dedication")
public class CpuDedicationExpectation @JsonCreator constructorconstructor(
		override val id : UUID,
		override val level : ExpectationLevel,
        val dedicatedVCpus : List<Int>
                                                    ) : Expectation