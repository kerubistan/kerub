package com.github.kerubistan.kerub.planner.steps.vstorage.fs.convert.othercap

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.vstorage.fs.convert.AbstractConvertImage
import com.github.kerubistan.kerub.utils.update

/**
 * This conversion
 */
data class ConvertImage(
		override val virtualStorage: VirtualStorageDevice,
		override val fromAllocation : VirtualStorageAllocation,
		val newAllocation: VirtualStorageAllocation,
		val host: Host
) : AbstractConvertImage() {
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