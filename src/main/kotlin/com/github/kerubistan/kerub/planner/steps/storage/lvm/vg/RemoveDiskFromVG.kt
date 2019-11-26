package com.github.kerubistan.kerub.planner.steps.storage.lvm.vg

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.update
import io.github.kerubistan.kroki.collections.update
import java.math.BigInteger.ZERO

@JsonTypeName("vg-remove-disk")
data class RemoveDiskFromVG(
		val capability: LvmStorageCapability,
		val device: String,
		val host: Host
) : AbstractOperationalStep {

	init {
		val capabilities = requireNotNull(host.capabilities) {
			"The host ${host.id} ${host.address} does not have any capabilities registered"
		}
		check(capability.id in capabilities.index.storageCapabilitiesById.keys) {
			"The host ${host.id} ${host.address} does not have " +
					"lvm capability ${capability.id} ${capability.volumeGroupName} registered."
		}
		check(capability.physicalVolumes.containsKey(device)) {
			"The lvm capability does not have a pv $device registered. " +
					"Registered devices are ${capability.physicalVolumes.keys}"
		}
		check(capability.physicalVolumes.size > 1) {
			"Can't remove the only PV from the VG"
		}
	}

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) { host ->
				val failingPeSize = capability.physicalVolumes
						.getValue(device)
				host.copy(
						stat = host.stat.copy(
								capabilities = host.stat.capabilities!!.copy(
										storageCapabilities = host.stat.capabilities.storageCapabilities.update(
												selector = { it.id == capability.id },
												map = {
													capability.copy(
															physicalVolumes = capability.physicalVolumes - device,
															size = (capability.size - failingPeSize).coerceAtLeast(ZERO)
													)
												}
										)
								)
						),
						dynamic = host.dynamic?.copy(
								storageStatus = host.dynamic.storageStatus.map {
									if (it is CompositeStorageDeviceDynamic && it.id == capability.id) {
										it.copy(
												reportedFreeCapacity = (it.freeCapacity - failingPeSize).coerceAtLeast(ZERO)
										)
									} else it
								}
						)
				)
			}
	)

	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))
}