package com.github.kerubistan.kerub.planner.costs

import com.github.kerubistan.kerub.model.Host

data class ComputationCost(
		val host: Host,
		val cycles: Long) : Cost