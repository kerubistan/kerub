package com.github.K0zka.kerub.planner.reservations

import com.github.K0zka.kerub.model.Host

class UseHostReservation(
		override val host: Host
) : HostReservation {
	override fun isShared() = true
}
