package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.Host

interface HostReservation : Reservation<Host> {
	val host: Host
	override fun entity() = host
}