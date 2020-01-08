package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.remove

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.network.ovs.sw.create.CreateOvsSwitch
import io.github.kerubistan.kroki.collections.update

@JsonTypeName("remove-ovs-switch")
data class RemoveOvsSwitch(
		val host: Host, val virtualNetwork: VirtualNetwork
) : AbstractOperationalStep, InvertibleStep {

	override fun isInverseOf(other: AbstractOperationalStep): Boolean =
			other is CreateOvsSwitch && other.isInverseOf(this)

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) { hostData ->
				hostData.copy(
						config = requireNotNull(hostData.config).copy(
								networkConfiguration = hostData.config.networkConfiguration.filterNot {
									it.virtualNetworkId == virtualNetwork.id
								}
						)
				)
			}
	)

	override fun reservations() = listOf(UseHostReservation(host))
}