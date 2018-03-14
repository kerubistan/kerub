package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon

import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.AbstractNfsStep

abstract class AbstractNfsDaemonStep : AbstractNfsStep() {
	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))
}
