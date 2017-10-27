package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.Host
import java.math.BigInteger

data class HostStorageReservation(
		override val host: Host,
		override val reservedStorage: BigInteger
) : HostReservation, StorageReservation<Host> {
	override fun isShared() = true
}