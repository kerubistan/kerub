package com.github.kerubistan.kerub.planner.costs

import java.util.Comparator

object TimeCostComparator : Comparator<TimeCost> {

	private fun avg(cost: TimeCost) = (cost.minMs + cost.maxMs) / 2

	override fun compare(first: TimeCost, second: TimeCost): Int
			= avg(first) - avg(second)
}