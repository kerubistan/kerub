package com.github.kerubistan.kerub.planner.steps.vstorage.fs.rebase

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.IOCost
import com.github.kerubistan.kerub.planner.costs.TimeCost
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.update
import java.math.BigInteger

data class FallocateImage(
		val virtualStorage: VirtualStorageDevice,
		val expectedFree : BigInteger,
		val allocation: VirtualStorageFsAllocation,
		val host: Host
) : AbstractOperationalStep {

	override fun take(state: OperationalState): OperationalState =
			state.copy(
				hosts = state.hosts.update(host.id) {
					it.copy(

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

	override fun reservations(): List<Reservation<*>>
			= listOf(
			UseHostReservation(host),
			VirtualStorageReservation(virtualStorage)
	)
}