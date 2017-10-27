package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.VirtualMachine

/**
 * VM Reservation requests that the VM should not be used by any other operations at the time.
 */
data class VmReservation(
		val vm: VirtualMachine
) : Reservation<VirtualMachine> {
	override fun entity() = vm

	override fun isShared(): Boolean = false
}