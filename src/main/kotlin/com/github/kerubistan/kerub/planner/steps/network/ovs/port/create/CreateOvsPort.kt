package com.github.kerubistan.kerub.planner.steps.network.ovs.port.create

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import io.github.kerubistan.kroki.collections.update
import io.github.kerubistan.kroki.collections.updateInstances

@JsonTypeName("create-ovs-port")
data class CreateOvsPort(
		val host: Host,
		val virtualNetwork: VirtualNetwork,
		val portName: String
) : AbstractOperationalStep {
	override fun take(state: OperationalState) = state.copy(
			hosts = state.hosts.update(host.id) { hostData ->
				hostData.copy(
						config = hostData.config?.let { hostCfg ->
							hostCfg.copy(
									networkConfiguration = hostCfg.networkConfiguration.updateInstances(
											selector = { ovsConfig: OvsNetworkConfiguration -> ovsConfig.virtualNetworkId == virtualNetwork.id },
											map = { ovsConfig -> ovsConfig.copy(ports = ovsConfig.ports + OvsDataPort(name = portName)) }
									)
							)
						}
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))
}