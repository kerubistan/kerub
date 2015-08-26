package com.github.K0zka.kerub.model

/**
 * The expectation level tells the priority of an expectation when evaluating constraints.
 */
public enum class ExpectationLevel {
	/**
	 * Wish can be ignored if no other way to get around.
	 */
	Wish,
	/**
	 * As long as the expectation is not satisfied, the contract is not met - therefore user should not be charged.
	 */
	Want,
	/**
	 * If an expectation is deal-breaker, the resource can not be used unless the expectation is satisfied
	 */
	DealBreaker
}