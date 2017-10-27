package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.update

interface AbstractIscsiShare : AbstractOperationalStep {
	val host: Host
	val vstorage: VirtualStorageDevice

	override fun take(state: OperationalState): OperationalState
			= state.copy(
			hosts = state.hosts.update(host.id, {
				hostData ->
				val hostConfig = hostData.config
				val service = IscsiService(vstorage.id)
				hostData.copy(
						config = updateHostConfig(hostConfig, service)
				)
			})
	)

	override fun reservations(): List<Reservation<*>> {
		return listOf(VirtualStorageReservation(device = vstorage), UseHostReservation(host = host))
	}

	private fun updateHostConfig(hostConfig: HostConfiguration?, service: IscsiService): HostConfiguration {
		return if (hostConfig == null) {
			HostConfiguration(id = host.id, services = listOf(service))
		} else {
			hostConfig.copy(
					services = hostConfig.services + service)
		}
	}


}