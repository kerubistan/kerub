package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.issues.problems.HostProblem

data class CpuOverheat(override val host: Host, val cores: Map<Int, Int>) : HostProblem