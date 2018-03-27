package com.github.kerubistan.kerub.planner.steps.vstorage.mount

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.services.HostService
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation

data class MountNfs(override val host: Host, val remoteHost: Host, val directory: String, val remoteDirectory: String) :
		AbstractNfsMount() {

	override fun updateHostConfiguration(config: HostConfiguration): List<HostService> = config.services + NfsMount(
			remoteHostId = remoteHost.id,
			localDirectory = directory,
			remoteDirectory = remoteDirectory
	)

	override fun reservations(): List<Reservation<*>> = super.reservations() + listOf(
			UseHostReservation(remoteHost)
	)

}