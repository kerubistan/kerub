package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.Host

data class FullHostReservation(
		override val host: Host
) : HostReservation {
	override fun isShared() = false
}