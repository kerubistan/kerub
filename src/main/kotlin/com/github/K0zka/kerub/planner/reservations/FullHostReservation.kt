package com.github.K0zka.kerub.planner.reservations

import com.github.K0zka.kerub.model.Host

data class FullHostReservation(
		override val host: Host
) : HostReservation