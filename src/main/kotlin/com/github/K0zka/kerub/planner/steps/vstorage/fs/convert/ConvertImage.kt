package com.github.K0zka.kerub.planner.steps.vstorage.fs.convert

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.utils.update

data class ConvertImage(
		val virtualStorage: VirtualStorageDevice,
		val newAllocation: VirtualStorageFsAllocation,
		val host: Host
) : AbstractOperationalStep {
	override fun take(state: OperationalState): OperationalState =
			state.copy(
					vStorage = state.vStorage.update(virtualStorage.id) {
						virtualStorageDataCollection ->
						virtualStorageDataCollection.copy(
								dynamic = requireNotNull(virtualStorageDataCollection.dynamic).copy(
										allocation = newAllocation
								)
						)
					}
			)

	override fun reservations() = listOf(
			UseHostReservation(host),
			VirtualStorageReservation(virtualStorage)
	)
}