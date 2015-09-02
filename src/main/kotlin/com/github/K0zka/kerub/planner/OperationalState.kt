package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.k0zka.finder4j.backtrack.State

data class OperationalState(
		val hosts: List<Host>,
		val hostDyns: List<HostDynamic>,
		val vms: List<VirtualMachine>,
		val vmDyns: List<VirtualMachineDynamic>
                           ) : State {
	override fun isComplete(): Boolean {
		throw UnsupportedOperationException()
	}
}