package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.Host
import java.math.BigInteger
import java.util.UUID

data class HostStorageReservation(
		override val host: Host,
		override val reservedStorage: BigInteger,
		val storageCapabilityId : UUID
) : HostReservation, StorageReservation<Host> {
	override fun isShared() = true
}