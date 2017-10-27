package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

@JsonTypeName("storage-redundancy")
data class StorageRedundancyExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		val outOfBox: Boolean = false,
		val nrOfCopies: Int
) : VirtualStorageExpectation