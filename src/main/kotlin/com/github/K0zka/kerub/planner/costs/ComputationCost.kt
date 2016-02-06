package com.github.K0zka.kerub.planner.costs

import com.github.K0zka.kerub.model.Host

class ComputationCost(
		val host: Host,
		val cycles: Long) : Cost