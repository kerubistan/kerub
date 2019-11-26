package com.github.kerubistan.kerub.planner.steps.network.ovs.port.remove

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.hasAny
import io.github.kerubistan.kroki.collections.concat
import kotlin.reflect.KClass

object RemoveOvsPortFactory : AbstractOperationalStepFactory<RemoveOvsPort>() {

	override fun produce(state: OperationalState) = state.index.runningHosts.mapNotNull { host ->
		host.config?.networkConfiguration?.filterIsInstance<OvsNetworkConfiguration>()?.mapNotNull { ovsConfig ->
			ovsConfig.ports.filterIsInstance<OvsDataPort>().mapNotNull { port ->
				if (portNotUsed(port, ovsConfig, state, host)) {
					RemoveOvsPort(
							host = host.stat,
							virtualNetwork = state.vNet.getValue(ovsConfig.virtualNetworkId),
							portName = port.name
					)
				} else null
			}
		}?.concat()
	}.concat()

	private fun portNotUsed(
			port: OvsDataPort,
			ovsConfig: OvsNetworkConfiguration,
			state: OperationalState,
			host: HostDataCollection): Boolean =
			state.vmsOnHost(host.id).none { vm ->
				vm.idStr == port.name && vm.devices.hasAny<NetworkDevice> { it.networkId == ovsConfig.virtualNetworkId }
			}

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()
}