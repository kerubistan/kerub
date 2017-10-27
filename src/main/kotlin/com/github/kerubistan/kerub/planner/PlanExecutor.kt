package com.github.kerubistan.kerub.planner

interface PlanExecutor {
	fun execute(plan: Plan, callback: (Plan) -> Unit)
}