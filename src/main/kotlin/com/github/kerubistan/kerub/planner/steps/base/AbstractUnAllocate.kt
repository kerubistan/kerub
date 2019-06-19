package com.github.kerubistan.kerub.planner.steps.base

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.update

abstract class AbstractUnAllocate<T : VirtualStorageAllocation> : AbstractOperationalStep {
	abstract val vstorage: VirtualStorageDevice
	abstract val allocation: T
	abstract val host: Host

	override fun take(state: OperationalState) =
			state.copy(
					vStorage = state.vStorage.update(vstorage.id) { vStorage ->
						vStorage.updateWithDynamic {dyn ->
							dyn.copy(allocations = dyn.allocations - allocation)
						}
					},
					hosts = state.hosts.update(host.id) { host ->
						host.updateWithDynamic {
							it.copy(
									storageStatus = it.storageStatus.map {
										it // TODO update host storage
									}
							)
						}
					}
			)

	override fun reservations(): List<Reservation<*>> =
			listOf(UseHostReservation(host), VirtualStorageReservation(vstorage))

}