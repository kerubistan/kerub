package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.costs.Risk
import com.github.kerubistan.kerub.planner.costs.TimeCost
import com.github.kerubistan.kerub.planner.costs.Violation
import io.github.kerubistan.kroki.collections.join

/**
 * Compares two plans by costs (time, IO, risk), violations, outcome.
 *
 * This comparison could be quite complex if it could consider all
 * aspects. E.g. a few gigs of disk write on an otherwise idle server
 * should be still better than doing the same on a heavily loaded server.
 *
 * For now something basic should do, just to introduce the concept that
 * plans are not equally good.
 *
 * If the costs are equal or we have no idea for a comparison yet, then
 * the shorter plan is better.
 */
object PlanComparator : Comparator<Plan> {

	private val Plan.costs get() = this.steps.map { it.getCost() }.join().groupBy { it.javaClass.kotlin }

	/**
	 * The order that tells which cost types to compare first when comparing plans.
	 * Therefore e.g. a plan with violations should be evaluated as less good than
	 * one without violations.
	 */
	private val costTypeOrder = listOf(
			Violation::class,
			Risk::class,
			TimeCost::class
	)

	override fun compare(first: Plan, second: Plan): Int {
		val firstCosts = first.costs
		val secondCosts = second.costs

		val costTypes = (firstCosts.keys + secondCosts.keys).sortedBy {
			val index = costTypeOrder.indexOf(it)
			if(index == -1) {
				costTypeOrder.size + 1
			} else {
				index
			}
		}

		costTypes.forEach { costType ->
			val first = firstCosts[costType] ?: listOf()
			val second = firstCosts[costType] ?: listOf()

			//TODO compare two list of costs, they are of the same type

		}

		//lots of things to do

		return first.steps.size.compareTo(second.steps.size)
	}
}