package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

data class CreateImage(
		val device: VirtualStorageDevice,
		val host: Host,
		val path : String,
		val format : VirtualDiskFormat) : AbstractOperationalStep {

	/*
	 * TODO: add costs here:
	 * - overallocation and underallocation of
	 * - bandwidth and storage capacity
	 */

	override fun reservations(): List<Reservation<*>>
			= listOf(
			VirtualStorageReservation(device),
			UseHostReservation(host)
	)

	override fun take(state: OperationalState): OperationalState =
			state.copy(
					vStorageDyns = state.vStorageDyns
							+ (device.id to VirtualStorageDeviceDynamic(
							id = device.id,
							allocation = VirtualStorageFsAllocation(
									hostId = host.id,
									mountPoint = "",
									type = VirtualDiskFormat.qcow2
							),
							actualSize = device.size //TODO not true when thin provisioning
					))
			)

}