package com.github.kerubistan.kerub.planner.steps.storage.fs.fallocate

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.IOCost
import com.github.kerubistan.kerub.planner.costs.TimeCost
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import io.github.kerubistan.kroki.collections.update
import java.math.BigInteger

/**
 * fallocate can run on existing files and free unused blocks.
 * Execution es heavily read-intensive, the bigger the allocated size is, the more
 */
@JsonTypeName("fallocate-image")
data class FallocateImage(
		val virtualStorage: VirtualStorageDevice,
		val expectedFree: BigInteger,
		val allocation: VirtualStorageFsAllocation,
		val host: Host
) : AbstractOperationalStep {

	override fun take(state: OperationalState): OperationalState =
			state.copy(
					hosts = state.hosts.update(host.id) {
						it.copy(
								dynamic = it.dynamic!!.copy(
										storageStatus = it.dynamic.storageStatus.map { storageDeviceDynamic ->
											if (storageDeviceDynamic.id == allocation.capabilityId) {
												//since this works on FS, and FS only does SimpleStorageDynamic
												(storageDeviceDynamic as SimpleStorageDeviceDynamic).copy(
														freeCapacity = storageDeviceDynamic.freeCapacity + expectedFree
												)
											} else storageDeviceDynamic
										}
								)
						)
					},
					vStorage = state.vStorage.update(virtualStorage.id) { storage ->
						storage.copy(
								dynamic = storage.dynamic!!.copy(
										allocations = storage.dynamic.allocations.map {
											if (it is VirtualStorageFsAllocation && it.capabilityId == allocation.capabilityId) {
												it.copy(
														actualSize = (it.actualSize - expectedFree)
																.coerceAtLeast(BigInteger.ZERO)
												)
											} else it
										}
								)
						)
					}
			)

	override fun getCost(): List<Cost> = listOf(
			IOCost(
					bytes = virtualStorage.size.toInt(), // TODO: no, only the actual size
					host = host
			),
			TimeCost(
					/* actual size / available IO */
					minMs = 0,
					maxMs = 0
			)
	)

	override fun reservations(): List<Reservation<*>> = listOf(
			UseHostReservation(host),
			VirtualStorageReservation(virtualStorage)
	)
}