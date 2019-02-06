package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.inplace

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.HostStorageReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.storage.fs.convert.AbstractConvertImage
import com.github.kerubistan.kerub.utils.update

data class InPlaceConvertImage(
		override val virtualStorage: VirtualStorageDevice,
		override val sourceAllocation: VirtualStorageFsAllocation,
		val host: Host,
		val targetFormat : VirtualDiskFormat
) : AbstractConvertImage() {

	init {
		check(host.id == sourceAllocation.hostId) {
			"allocation host id (${sourceAllocation.hostId}) does not match the host id (${host.id})"
		}
		check(host.capabilities?.storageCapabilitiesById?.keys?.let { sourceAllocation.capabilityId in it } ?: false) {
			"source allocation refers to capability (${sourceAllocation.capabilityId}) not registered in the host record." +
					"known capabilities are ${host.capabilities?.storageCapabilitiesById}"
		}
		check(sourceAllocation.type != targetFormat) {
			"It is pointless to have the same format as source and target ($targetFormat)"
		}
	}

	override fun take(state: OperationalState): OperationalState = state.copy(
			vStorage = state.vStorage.update(virtualStorage.id) {
				it.copy(
						dynamic = it.dynamic!!.copy(
								allocations = it.dynamic.allocations - sourceAllocation + sourceAllocation.copy(type = targetFormat)
						)
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(
			//TODO: the allocation should be reserved exclusively
			UseHostReservation(host),
			HostStorageReservation(
					host = host,
					storageCapabilityId = sourceAllocation.capabilityId,
					// this may need some more precision
					// - consider the overhead of the format
					// - consider thin allocation if applicable to source / target
					reservedStorage = virtualStorage.size
			)
	)
}