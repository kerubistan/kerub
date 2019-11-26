package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.create

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.network.ovs.sw.remove.RemoveOvsSwitch
import io.github.kerubistan.kroki.collections.update

@JsonTypeName("create-ovs-switch")
data class CreateOvsSwitch(
		val host: Host,
		val network: VirtualNetwork
) : AbstractOperationalStep, InvertibleStep {

	override fun isInverseOf(other: AbstractOperationalStep): Boolean =
			other is RemoveOvsSwitch
					&& other.host.id == this.host.id
					&& other.virtualNetwork.id == this.network.id

	override fun take(state: OperationalState) = state.copy(
			hosts = state.hosts.update(host.id) { hostData ->
				hostData.copy(
						config = hostData.config?.copy(
								networkConfiguration = hostData.config.networkConfiguration
										+ OvsNetworkConfiguration(virtualNetworkId = network.id)
						)
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))
}