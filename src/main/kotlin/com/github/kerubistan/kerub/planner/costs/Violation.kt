package com.github.kerubistan.kerub.planner.costs

import com.github.kerubistan.kerub.model.Expectation

/**
 * States that the operation can be executed at the cost of violation of an expectation.
 */
data class Violation(val expectation: Expectation) : Cost