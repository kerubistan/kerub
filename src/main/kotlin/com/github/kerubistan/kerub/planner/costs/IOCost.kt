package com.github.kerubistan.kerub.planner.costs

import com.github.kerubistan.kerub.model.Host

data class IOCost(val bytes: Int, val host: Host) : Cost