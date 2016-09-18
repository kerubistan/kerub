package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.ctld

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

class CtldIscsiShare(val host: Host, val virtualStorage: VirtualStorageDevice) : AbstractOperationalStep {
	override fun reservations(): List<Reservation<*>>
			= listOf(UseHostReservation(host), VirtualStorageReservation(virtualStorage))

	override fun take(state: OperationalState): OperationalState {
		TODO()
	}
}