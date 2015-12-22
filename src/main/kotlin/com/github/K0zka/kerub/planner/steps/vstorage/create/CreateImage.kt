package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

data class CreateImage(val device: VirtualStorageDevice, val host: Host) : AbstractOperationalStep {

	override fun reservations(): List<Reservation>
			= listOf(VirtualStorageReservation(device))

	override fun take(state: OperationalState): OperationalState =
			state.copy(
					vStorageDyns = state.vStorageDyns
							+ (device.id to VirtualStorageDeviceDynamic(
							id = device.id,
							hostId = host.id,
							actualSize = device.size //TODO not true when thin provisioning
					))
			)

}