package com.github.kerubistan.kerub.planner.steps.vstorage.fs.convert

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.update

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
										allocations = listOf(newAllocation)
								)
						)
					}
			)

	override fun reservations() = listOf(
			UseHostReservation(host),
			VirtualStorageReservation(virtualStorage)
	)
}