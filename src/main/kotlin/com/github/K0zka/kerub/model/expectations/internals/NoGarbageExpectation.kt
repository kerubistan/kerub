package com.github.K0zka.kerub.model.expectations.internals

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.InternalExpectation

/**
 * Internal expectation stating that there should not be any dead/deleted hosts left in the system.
 */
@JsonTypeName("no-garbage")
data class NoGarbageExpectation(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		val host: Host
) : InternalExpectation