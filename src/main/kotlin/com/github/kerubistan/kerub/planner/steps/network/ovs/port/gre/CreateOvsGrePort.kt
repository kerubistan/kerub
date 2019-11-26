package com.github.kerubistan.kerub.planner.steps.network.ovs.port.gre

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import io.github.kerubistan.kroki.collections.update
import io.github.kerubistan.kroki.collections.updateInstances

@JsonTypeName("create-ovs-gre-port")
data class CreateOvsGrePort(
		val firstHost: Host,
		val secondHost: Host,
		val virtualNetwork: VirtualNetwork,
		val name: String) : AbstractOperationalStep {

	init {
		check(firstHost.id != secondHost.id) {
			"First host (${firstHost.id}) must not be the same as the second host (${secondHost.id})"
		}
	}

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(firstHost.id) { host ->
				host.copy(
						config = requireNotNull(host.config).copy(
								networkConfiguration = host.config.networkConfiguration.updateInstances(
										selector = { ovsConfig: OvsNetworkConfiguration ->
											ovsConfig.virtualNetworkId == virtualNetwork.id
										},
										map = { ovsConfig ->
											ovsConfig.copy(
													ports = ovsConfig.ports + OvsGrePort(
															name = name,
															remoteAddress = secondHost.address
													)
											)
										}
								)
						)
				)
			}
	)

	override fun reservations() = listOf(UseHostReservation(firstHost), UseHostReservation(secondHost))
}