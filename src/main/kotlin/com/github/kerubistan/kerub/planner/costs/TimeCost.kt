package com.github.kerubistan.kerub.planner.costs

data class TimeCost(
		val minMs: Int,
		val maxMs: Int) : Cost {

	init {
		require(minMs >= 0) { "Minimum ($minMs) must be greater or equal to zero" }
		require(maxMs >= minMs) { "Maximum ($maxMs) must be greater or equal to minimum ($minMs)" }
	}

	operator fun plus(other: TimeCost) = TimeCost(minMs = this.minMs + other.minMs, maxMs = this.maxMs + other.maxMs)
}