package com.github.kerubistan.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.ExpectationLevel

/**
 *
 */
@JsonTypeName("pool-average-load")
data class PoolAverageLoadExpectation(val max: Int = 90,
									  val min: Int = 10,
									  val toleranceMs: Int = 10000,
									  override val level: ExpectationLevel = ExpectationLevel.Want) : PoolExpectation {
	companion object {
		val loadRange = 1..100
	}

	init {
		check(max > min) { "Maximum load ($max) must be larger than minimum ($min)" }
		check(max in loadRange) { "Maximum load ($max) must be between 0 and 100" }
		check(min in loadRange) { "Minimum load ($min) must be between 0 and 100" }
		check(toleranceMs > 0) { "Tolerance must be a positive number" }
	}
}