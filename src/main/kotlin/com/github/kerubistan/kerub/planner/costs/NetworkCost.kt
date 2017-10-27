package com.github.kerubistan.kerub.planner.costs

import com.github.kerubistan.kerub.model.Host

data class NetworkCost(val bytes: Long, val hosts: List<Host>) : Cost