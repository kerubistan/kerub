package com.github.kerubistan.kerub.planner.issues.problems.vms

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.issues.problems.HostProblem

data class VmOnRecyclingHost(val vm: VirtualMachine, override val host: Host) : HostProblem