package com.github.K0zka.kerub.model.expectations

/**
 * The expectation level tells the priority of an expectation when evaluating constraints.
 */
public enum class ExpectationLevel {
	/**
	 * Hints can be ignored if no other way to get around
	 */
	Hint
	/**
	 * If an expectation is deal-breaker, the resource can not be used unless the expectation is satisfied
	 */
	DealBreaker
}