package com.github.kerubistan.kerub.planner

/**
 * A plan rationalizer removes the redundant steps from a plan.
 */
interface PlanRationalizer {
	/**
	 * rationalize a plan
	 * @return the rationalized plan
	 */
	fun rationalize(plan: Plan): Plan
}