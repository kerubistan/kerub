package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.othercap

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.GvinumStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.lvm.LvmStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.storage.fs.convert.AbstractConvertImage
import com.github.kerubistan.kerub.utils.update
import java.math.BigInteger

/**
 * This conversion moves the
 */
data class ConvertImage(
		override val virtualStorage: VirtualStorageDevice,
		override val sourceAllocation: VirtualStorageAllocation,
		val host: Host,
		val targetAllocation: VirtualStorageAllocation
) : AbstractConvertImage() {

	init {
		check(targetAllocation.hostId == host.id) {
			"target allocation host id (${targetAllocation.hostId}) must be the same as " +
					"host id (${host.id})"
		}
		check(sourceAllocation.hostId == host.id) {
			"source allocation host id (${sourceAllocation.hostId}) must be the same as " +
					"host id (${host.id})"
		}
	}

	override fun take(state: OperationalState): OperationalState {
		return state.copy(
				vStorage = state.vStorage.update(virtualStorage.id) { virtualStorageDataCollection ->
					virtualStorageDataCollection.copy(
							dynamic = requireNotNull(virtualStorageDataCollection.dynamic).let {
								it.copy(
										allocations = if (!virtualStorage.readOnly) {
											listOf(targetAllocation)
										} else {
											it.allocations + targetAllocation
										}
								)
							}
					)
				},
				hosts = state.hosts.update(host.id) { hostDataCollection ->
					hostDataCollection.copy(
							dynamic = requireNotNull(hostDataCollection.dynamic).let { hostDyn ->
								hostDyn.copy(
										storageStatus = hostDyn.storageStatus.update(
												selector = { storageStatus -> storageStatus.id == targetAllocation.capabilityId },
												map = { storageStatus ->
													when (storageStatus) {
														is SimpleStorageDeviceDynamic -> storageStatus.copy(
																// TODO this is again an inaccurate but safe estimate
																// as it depends on the target format and
																// the actual size
																freeCapacity = (storageStatus.freeCapacity
																		- virtualStorage.size)
																		.coerceAtLeast(BigInteger.ZERO)
														)
														is GvinumStorageDeviceDynamic -> TODO()
														is LvmStorageDeviceDynamic -> storageStatus.copy(
																items = storageStatus.items.map {
																	TODO("map the allocations")
																}
														)
														else -> TODO(
																"Not implemented with ${storageStatus.javaClass.name}")
													}
												})
								)
							}
					)
				}
		)

	}

	override fun reservations() = listOf(
			UseHostReservation(host),
			VirtualStorageReservation(virtualStorage)
	)
}