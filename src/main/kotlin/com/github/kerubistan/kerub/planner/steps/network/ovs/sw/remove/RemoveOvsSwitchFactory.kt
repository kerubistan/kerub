package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.remove

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.mapInstances
import io.github.kerubistan.kroki.collections.concat
import java.util.UUID
import kotlin.reflect.KClass

object RemoveOvsSwitchFactory : AbstractOperationalStepFactory<RemoveOvsSwitch>() {
	override fun produce(state: OperationalState) = state.index.runningHosts.mapNotNull { host ->
		val vmsOnHost = state.vmsOnHost(host.id)
		host.config?.networkConfiguration?.mapInstances { networkConfig: OvsNetworkConfiguration ->
			if (networkNotUsed(networkConfig.virtualNetworkId, vmsOnHost)) {
				RemoveOvsSwitch(
						host = host.stat,
						virtualNetwork = state.vNet.getValue(networkConfig.virtualNetworkId)
				)
			} else null
		}
	}.concat()

	private fun networkNotUsed(virtualNetworkId: UUID, vmsOnHost: List<VirtualMachine>): Boolean =
			vmsOnHost.all { vm ->
				vm.devices.none { device -> device is NetworkDevice && device.networkId == virtualNetworkId }
			}

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()
}