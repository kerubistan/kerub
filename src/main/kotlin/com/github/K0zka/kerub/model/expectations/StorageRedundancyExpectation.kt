package com.github.K0zka.kerub.model.expectations

import com.github.K0zka.kerub.model.Expectation
import java.util.UUID
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel

JsonTypeName("storage-redundancy")
data class StorageRedundancyExpectation @JsonCreator constructorconstructor(
		override val id: UUID,
		override val level : ExpectationLevel = ExpectationLevel.DealBreaker,
		val nrOfCopies: Int
                                          ) : Expectation