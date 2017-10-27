package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.Host

class UseHostReservation(
		override val host: Host
) : HostReservation {
	override fun isShared() = true
}
