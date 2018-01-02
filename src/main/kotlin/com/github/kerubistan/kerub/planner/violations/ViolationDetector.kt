package com.github.kerubistan.kerub.planner.violations

import com.github.kerubistan.kerub.model.Constrained
import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState

/**
 * Checks for a certain type of expectation in the state of the cluster.
 * @param T the constrained entity type
 * @param E the expectation type to check
 */
interface ViolationDetector<in T : Constrained<*>, in E : Expectation> {
	/**
	 * Check an constrained entity for violations of one of its expectations.
	 * @param entity the constrained entity (vm, vnet, vstorage)
	 * @param expectation the expectation to check
	 * @param state the complete operational state
	 */
	fun check(entity : T, expectation: E, state : OperationalState) : Boolean
}