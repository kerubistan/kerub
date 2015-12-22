package com.github.K0zka.kerub.planner.reservations

import com.github.K0zka.kerub.model.VirtualMachine

/**
 * VM Reservation requests that the VM should not be used by any other operations at the time.
 */
data class VmReservation(
		val vm : VirtualMachine
) : Reservation