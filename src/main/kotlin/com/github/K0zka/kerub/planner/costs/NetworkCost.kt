package com.github.K0zka.kerub.planner.costs

import com.github.K0zka.kerub.model.Host

data class NetworkCost(bytes : Long, hosts : List<Host>) : Cost