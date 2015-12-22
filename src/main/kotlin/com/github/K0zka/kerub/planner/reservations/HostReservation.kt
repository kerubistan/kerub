package com.github.K0zka.kerub.planner.reservations

import com.github.K0zka.kerub.model.Host

interface HostReservation : Reservation {
	val host : Host
}