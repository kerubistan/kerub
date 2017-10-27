package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.VirtualStorageDevice

data class VirtualStorageReservation(val device: VirtualStorageDevice) : Reservation<VirtualStorageDevice> {
	override fun entity() = device

	override fun isShared() = false
}