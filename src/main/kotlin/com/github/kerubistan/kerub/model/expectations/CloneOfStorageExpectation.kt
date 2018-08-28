package com.github.kerubistan.kerub.model.expectations

import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.InternalExpectation
import java.util.UUID

/**
 * An internal expectation that instructs the planner to create the storage with the data from an original disk.
 */
data class CloneOfStorageExpectation(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		val sourceStorageId: UUID
) : InternalExpectation, VirtualStorageExpectation