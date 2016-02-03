package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel
import java.util.UUID

@JsonTypeName("not-same-storage")
data class NotSameStorageExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		val otherDiskIds: List<UUID>
) : VirtualStorageExpectation