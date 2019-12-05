package com.github.kerubistan.kerub.planner.issues.problems.vnet

import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.utils.hasNone
import io.github.kerubistan.kroki.collections.concat

/**
 * If at any point in the plan, there are running VMs connected to a virtual network
 * with virtual switches on multiple nodes that aren't connected with tunnels, then that network
 * is partitioned and that must be detected.
 */
object VirtualNetworkPartitionedDetector : ProblemDetector<VirtualNetworkPartitioned> {
	override fun detect(plan: Plan): Collection<VirtualNetworkPartitioned> =
			plan.states.map { state ->
				state.index.hostsByVirtualNetworks.mapNotNull { (networkId, hosts) ->
					hosts.map { sourceHost ->
						(hosts - sourceHost).mapNotNull { targetHost ->
							if (sourceHost.config?.index?.ovsNetworkConfigurations
											?.get(networkId)?.ports
											?.hasNone<OvsGrePort> { it.remoteAddress == targetHost.stat.address }
									== true) {
								VirtualNetworkPartitioned(
										isolatedHost = sourceHost.stat,
										virtualNetwork = state.vNet.getValue(networkId)
								)
							} else null
						}
					}.concat()
				}.concat()
			}.concat()
}