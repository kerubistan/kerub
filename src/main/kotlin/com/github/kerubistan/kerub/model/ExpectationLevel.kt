package com.github.kerubistan.kerub.model

import java.util.Comparator

/**
 * The expectation level tells the priority of an expectation when evaluating constraints.
 */
enum class ExpectationLevel {
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
	DealBreaker;

	object comparator : Comparator<ExpectationLevel> {

		private val values = mapOf(
				Wish to 1,
				Want to 2,
				DealBreaker to 3
		)

		override fun compare(first: ExpectationLevel, second: ExpectationLevel): Int =
				values[first]!! - values[second]!!

	}

}