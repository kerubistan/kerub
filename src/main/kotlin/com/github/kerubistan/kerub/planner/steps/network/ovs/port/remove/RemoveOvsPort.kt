package com.github.kerubistan.kerub.planner.steps.network.ovs.port.remove

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.network.ovs.port.create.CreateOvsPort
import com.github.kerubistan.kerub.utils.update
import io.github.kerubistan.kroki.collections.updateInstances

@JsonTypeName("remove-ovs-port")
data class RemoveOvsPort(
		val host: Host,
		val virtualNetwork: VirtualNetwork,
		val portName: String
) : AbstractOperationalStep, InvertibleStep {

	override fun isInverseOf(other: AbstractOperationalStep) = other is CreateOvsPort
			&& other.host.id == this.host.id
			&& other.virtualNetwork.id == this.virtualNetwork.id
			&& other.portName == this.portName

	override fun take(state: OperationalState) = state.copy(
			hosts = state.hosts.update(host.id) { host ->
				host.copy(
						config = host.config!!.copy(
								networkConfiguration = host.config.networkConfiguration.updateInstances(
										selector = { ovsConfig: OvsNetworkConfiguration -> ovsConfig.virtualNetworkId == virtualNetwork.id }
								)
								{ ovsConfig -> ovsConfig.copy(ports = ovsConfig.ports.filterNot { it.name == portName }) }
						)
				)
			}
	)

	override fun reservations() = listOf(UseHostReservation(host))
}