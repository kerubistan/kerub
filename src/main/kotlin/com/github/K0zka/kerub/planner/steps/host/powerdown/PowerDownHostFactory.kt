package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.planner.steps.factoryFeature
import java.util.UUID

/**
 * Generates PowerDown steps for each host in Up state if no virtual services run.
 */
object PowerDownHostFactory : AbstractOperationalStepFactory<PowerDownHost>() {
	override fun produce(state: OperationalState): List<PowerDownHost> =
			factoryFeature(state.controllerConfig.powerManagementEnabled) {
				// NOTE:
				// for now the only service is the running VM's storage, network
				// and other computational resources should also be included
				val hostsWithVms: List<UUID> = state.vms.values.filter {
					it.dynamic?.status != VirtualMachineStatus.Down
				}.map { it.dynamic?.hostId }.filterNotNull()

				val idleDedicatedHosts = state.hosts.filter {
					!hostsWithVms.contains(it.key)
							&& it.value.stat.dedicated
							&& it.value.config?.services?.isEmpty() ?: true
							&& it.value.stat.capabilities?.powerManagment?.let { it.isNotEmpty() } ?: false
				}

				idleDedicatedHosts.filter {
					val dyn = state.hosts[it.key]?.dynamic
					dyn?.status == HostStatus.Up
				}.map { PowerDownHost(it.value.stat) }
			}

}