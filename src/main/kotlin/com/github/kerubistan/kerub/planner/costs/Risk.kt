package com.github.kerubistan.kerub.planner.costs

/**
 * Risk is a a kind of cost that occurs when a Violation may occur in the future.
 */
data class Risk(
		/**
		 * The score of the risk. The bigger the score, the bigger the issue and should make more effort to avoid
		 */
		val score: Int,
		/**
		 * A comment about the nature of the risk
		 */
		val comment: String) : Cost {

	init {
		require(score > 0) { "Score ($score) must be greater than zero" }
	}

	operator fun plus(other: Risk) = Risk(score = this.score + other.score, comment = "$this\n$other")
}