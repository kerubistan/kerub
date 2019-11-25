package com.github.kerubistan.kerub.planner.steps.network.ovs.port.create

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.mapInstances
import io.github.kerubistan.kroki.collections.join
import java.util.UUID
import kotlin.reflect.KClass

object CreateOvsPortFactory : AbstractOperationalStepFactory<CreateOvsPort>() {
	override fun produce(state: OperationalState): List<CreateOvsPort> = state.index.vmsThatMustStart.mapNotNull { vm ->
		val requiredNetworks = vm.stat.devices.mapInstances { device: NetworkDevice -> device.networkId }
		state.index.runningHosts.filter {
			it.config?.networkConfiguration?.mapInstances { ovsConfig: OvsNetworkConfiguration -> ovsConfig.virtualNetworkId }?.containsAll(requiredNetworks)
					?: false
		}?.mapNotNull { host ->
			// this host has all virtual networks required by the vm, so we can create ports
			// but let's do this only for the ports not yet created
			requiredNetworks.mapNotNull { requiredNetworkId ->
				if (!portAllocated(host, requiredNetworkId, vm)) {
					CreateOvsPort(
							host = host.stat,
							portName = vm.stat.idStr,
							virtualNetwork = state.vNet.getValue(requiredNetworkId)
					)
				} else null
			}
		}.join()
	}.join()

	private fun portAllocated(host: HostDataCollection, requiredNetworkId: UUID, vm: VirtualMachineDataCollection) =
			host.config?.index?.ovsNetworkConfigurations?.getValue(requiredNetworkId)?.index?.portNames?.contains(vm.stat.idStr)
					?: false

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()
}