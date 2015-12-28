package com.github.K0zka.kerub.planner

public interface PlanExecutor {
	fun execute(plan: Plan, callback: (Plan) -> Unit)
}