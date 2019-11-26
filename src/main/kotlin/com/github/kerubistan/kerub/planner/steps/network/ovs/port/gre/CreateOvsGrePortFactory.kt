package com.github.kerubistan.kerub.planner.steps.network.ovs.port.gre

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import io.github.kerubistan.kroki.collections.concat
import kotlin.reflect.KClass

object CreateOvsGrePortFactory : AbstractOperationalStepFactory<CreateOvsGrePort>() {
	override fun produce(state: OperationalState) = state.index.virtualNetworksNeeded.mapNotNull { neededNetworkId ->

		state.index.hostsByVirtualNetworks[neededNetworkId]?.map { host ->

			CreateOvsGrePort(
					firstHost = host.stat,
					secondHost = TODO(),
					virtualNetwork = TODO(),
					name = TODO()
			)
		}

	}.concat()

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()
}