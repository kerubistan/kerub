package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.k0zka.finder4j.backtrack.State

data class OperationalState(
		hosts: List<Host>,
		hostDyns: List<HostDynamic>,
		vms: List<VirtualMachine>,
		vmDyns: List<VirtualMachineDynamic>
                           ) : State {
	override fun isComplete(): Boolean {
		throw UnsupportedOperationException()
	}
}