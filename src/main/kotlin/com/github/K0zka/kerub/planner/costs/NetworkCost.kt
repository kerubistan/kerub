package com.github.K0zka.kerub.planner.costs

import com.github.K0zka.kerub.model.Host

data class NetworkCost(val bytes: Long, val hosts: List<Host>) : Cost