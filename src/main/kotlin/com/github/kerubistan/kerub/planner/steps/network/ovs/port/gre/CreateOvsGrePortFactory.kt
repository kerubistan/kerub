package com.github.kerubistan.kerub.planner.steps.network.ovs.port.gre

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.hasAny
import io.github.kerubistan.kroki.collections.concat
import java.util.UUID.randomUUID
import kotlin.reflect.KClass

object CreateOvsGrePortFactory : AbstractOperationalStepFactory<CreateOvsGrePort>() {
	override fun produce(state: OperationalState) = state.index.virtualNetworksNeeded.mapNotNull {
		// for each network needed by any client workload
		neededNetworkId ->

		val hosts = state.index.hostsByVirtualNetworks.getValue(neededNetworkId)

		hosts.map {
			//for each host that has the network
			sourceHost ->

			hosts.filter {
				// no to the same host, that is pointless of course
				targetHost ->
				targetHost.id != sourceHost.id
						&& sourceHost.config?.networkConfiguration?.none { networkConfig ->
					networkConfig is OvsNetworkConfiguration
							&& networkConfig.ports.hasAny<OvsGrePort> { it.remoteAddress == targetHost.stat.address }
				} ?: false
			}.map { targetHost ->

				CreateOvsGrePort(
						firstHost = sourceHost.stat,
						secondHost = targetHost.stat,
						virtualNetwork = state.vNet.getValue(neededNetworkId),
						name = "gre-${randomUUID()}"
				)

			}

		}.concat()

	}.concat()

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()
}