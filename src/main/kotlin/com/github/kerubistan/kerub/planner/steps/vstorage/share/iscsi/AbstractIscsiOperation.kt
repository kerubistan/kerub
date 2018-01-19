package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

interface AbstractIscsiOperation : AbstractOperationalStep {
	val host: Host
	val vstorage: VirtualStorageDevice

	override fun reservations(): List<Reservation<*>> =
			listOf(VirtualStorageReservation(device = vstorage), UseHostReservation(host = host))

}