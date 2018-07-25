package com.github.kerubistan.kerub.planner.issues.problems.vms

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.issues.problems.Problem

data class VmOnRecyclingHost(val vm: VirtualMachine, val host: Host) : Problem