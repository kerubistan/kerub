package com.github.K0zka.kerub.planner.steps.vstorage.fs.rebase

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.costs.IOCost
import com.github.K0zka.kerub.planner.costs.TimeCost
import com.github.K0zka.kerub.planner.reservations.Reservation
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.utils.update
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