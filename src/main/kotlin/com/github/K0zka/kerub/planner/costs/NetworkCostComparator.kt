package com.github.K0zka.kerub.planner.costs

import java.util.Comparator

public object NetworkCostComparator : Comparator<NetworkCost> {

	fun cost(networkCost: NetworkCost) = networkCost.bytes * (networkCost.hosts.size + 1)

	override fun compare(first: NetworkCost, second: NetworkCost): Int =
			(cost(first) - cost(second)).toInt()
}