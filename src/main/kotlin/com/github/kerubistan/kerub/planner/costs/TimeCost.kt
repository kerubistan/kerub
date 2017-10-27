package com.github.kerubistan.kerub.planner.costs

data class TimeCost(
		val minMs: Int,
		val maxMs: Int) : Cost