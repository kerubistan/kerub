package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.create

import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import io.github.kerubistan.kroki.collections.join
import kotlin.reflect.KClass

object CreateOvsSwitchFactory : AbstractOperationalStepFactory<CreateOvsSwitch>() {
	override fun produce(state: OperationalState): List<CreateOvsSwitch> = state.index.virtualNetworksNeeded.map {
		state.index.runningHosts.map { host ->
			val hostNetworks = host.config?.networkConfiguration?.map { it.virtualNetworkId }?.toSet() ?: setOf()
			(state.index.virtualNetworksNeeded - hostNetworks).map { networkId ->
				CreateOvsSwitch(
						host = host.stat,
						network = state.vNet.getValue(networkId)
				)
			}
		}
	}.join().join()

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf(
			VirtualMachineAvailabilityExpectation::class
	)
}