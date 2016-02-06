package com.github.K0zka.kerub.planner

interface PlanExecutor {
	fun execute(plan: Plan, callback: (Plan) -> Unit)
}