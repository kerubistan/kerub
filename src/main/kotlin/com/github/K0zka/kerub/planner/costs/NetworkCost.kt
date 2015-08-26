package com.github.K0zka.kerub.planner.costs

import com.github.K0zka.kerub.model.Host

data class NetworkCost(bytes : Int, hosts : List<Host>) : Cost