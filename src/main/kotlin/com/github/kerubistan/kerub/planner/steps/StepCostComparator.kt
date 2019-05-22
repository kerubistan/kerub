package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.planner.costs.ComputationCost
import com.github.kerubistan.kerub.planner.costs.ComputationCostComparator
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.IOCost
import com.github.kerubistan.kerub.planner.costs.IOCostComparator
import com.github.kerubistan.kerub.planner.costs.NetworkCost
import com.github.kerubistan.kerub.planner.costs.NetworkCostComparator
import com.github.kerubistan.kerub.planner.costs.Risk
import com.github.kerubistan.kerub.planner.costs.RiskComparator
import com.github.kerubistan.kerub.planner.costs.TimeCost
import com.github.kerubistan.kerub.planner.costs.TimeCostComparator
import java.util.Comparator
import kotlin.reflect.KClass

object StepCostComparator : Comparator<AbstractOperationalStep> {

	private val comparators = mapOf(
			Risk::class to RiskComparator,
			TimeCost::class to TimeCostComparator,
			IOCost::class to IOCostComparator,
			NetworkCost::class to NetworkCostComparator,
			ComputationCost::class to ComputationCostComparator
	)

	private fun findCost(step: AbstractOperationalStep, costClass: KClass<out Cost>): Cost? {
		return step.getCost().firstOrNull { it.javaClass.kotlin == costClass }
	}

	override fun compare(first: AbstractOperationalStep, second: AbstractOperationalStep): Int {
		for (costClass in comparators.keys) {
			val firstCost = findCost(first, costClass)
			val secondCost = findCost(second, costClass)

			if (firstCost != null) {
				if (secondCost != null) {
					val result = compareCosts(firstCost, secondCost)
					if (result != 0) {
						return result
					}
				} else {
					return 1
				}
			} else if (secondCost != null) {
				return -1
			}
		}
		return 0
	}

	private fun compareCosts(firstCost: Cost, secondCost: Cost): Int {
		//TODO: this mess is because of a possible bug
		// in compiler that prevents resolving the compare method of Comparator
		return when (firstCost) {
			is IOCost -> IOCostComparator.compare(firstCost, secondCost as IOCost)
			is NetworkCost -> NetworkCostComparator.compare(firstCost, secondCost as NetworkCost)
			is Risk -> RiskComparator.compare(firstCost, secondCost as Risk)
			is TimeCost -> TimeCostComparator.compare(firstCost, secondCost as TimeCost)
			is ComputationCost -> ComputationCostComparator.compare(firstCost, secondCost as ComputationCost)
			else -> throw IllegalArgumentException("Not handled cost type")
		}
	}
}