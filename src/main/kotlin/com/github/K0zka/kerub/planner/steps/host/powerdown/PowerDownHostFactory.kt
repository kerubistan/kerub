package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import java.util.UUID

/**
 * Generates PowerDown steps for each host in Up state if no virtual services run.
 */
object PowerDownHostFactory : AbstractOperationalStepFactory<PowerDownHost>() {
	override fun produce(state: OperationalState): List<PowerDownHost> {

		// NOTE:
		// for now the only service is the running VM's storage, network
		// and other computational resources should also be included
		val vmsOnHost: List<UUID> = state.vmDyns.filter {
			it.value.status != VirtualMachineStatus.Down
		}.map { it.value.hostId }

		val idleDedicatedHosts = state.hosts.filter { !vmsOnHost.contains(it.key) && it.value.dedicated }

		return idleDedicatedHosts.filter {
			val dyn = state.hostDyns[it.key]
			dyn?.status == HostStatus.Up
		}.map { PowerDownHost(it.value) }

	}
}