package com.github.kerubistan.kerub.planner.issues.problems.vnet

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.planner.issues.problems.Problem

data class VirtualNetworkPartitioned(val virtualNetwork: VirtualNetwork, val isolatedHost: Host) : Problem
