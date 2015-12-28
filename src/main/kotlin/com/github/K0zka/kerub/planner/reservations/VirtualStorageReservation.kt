package com.github.K0zka.kerub.planner.reservations

import com.github.K0zka.kerub.model.VirtualStorageDevice

data class VirtualStorageReservation(val device: VirtualStorageDevice) : Reservation<VirtualStorageDevice> {
	override fun entity() = device

	override fun isShared() = false
}