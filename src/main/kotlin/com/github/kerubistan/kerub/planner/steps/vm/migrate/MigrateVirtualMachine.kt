package com.github.kerubistan.kerub.planner.steps.vm.migrate

import com.github.k0zka.finder4j.backtrack.Step
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.Plan

interface MigrateVirtualMachine : Step<Plan> {
	val vm: VirtualMachine
	val source: Host
	val target: Host
}