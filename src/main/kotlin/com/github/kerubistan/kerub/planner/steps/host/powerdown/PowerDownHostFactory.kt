package com.github.kerubistan.kerub.planner.steps.host.powerdown

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHost
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import java.util.UUID
import kotlin.reflect.KClass

/**
 * Generates PowerDown steps for each host in Up state if no virtual services run.
 */
object PowerDownHostFactory : AbstractOperationalStepFactory<PowerDownHost>() {

	override val problemHints = setOf(RecyclingHost::class)

	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<PowerDownHost> =
			factoryFeature(state.controllerConfig.powerManagementEnabled) {
				// NOTE:
				// for now the only service is the running VM's storage, network
				// and other computational resources should also be included
				val hostsWithVms: List<UUID> = state.vms.values.filter {
					it.dynamic?.status != VirtualMachineStatus.Down
				}.mapNotNull { it.dynamic?.hostId }

				val idleDedicatedHosts = state.hosts.filter {
					!hostsWithVms.contains(it.key) && it.value.let {
						isHostIdle(it) && canPowerDown(it)
					}
				}

				idleDedicatedHosts.filter {
					val dyn = state.hosts[it.key]?.dynamic
					dyn?.status == HostStatus.Up
				}.map { PowerDownHost(it.value.stat) }
			}

	private fun canPowerDown(host: HostDataCollection): Boolean =
			(host.stat.dedicated
					&& host.stat.capabilities?.powerManagment?.isNotEmpty() ?: false
					) || host.stat.recycling

	private fun isHostIdle(host: HostDataCollection) =
			host.config?.services?.isEmpty() ?: true

}