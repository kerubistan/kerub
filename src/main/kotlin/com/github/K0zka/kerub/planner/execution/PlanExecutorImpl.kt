package com.github.K0zka.kerub.planner.execution

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.PlanExecutor

public class PlanExecutorImpl(val hostManager : HostManager) : PlanExecutor {
	override fun execute(plan: Plan) {
		throw UnsupportedOperationException()
	}
}