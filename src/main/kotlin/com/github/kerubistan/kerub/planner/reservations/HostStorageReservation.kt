package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.Host
import java.math.BigInteger
import java.util.UUID

data class HostStorageReservation(
		override val host: Host,
		override val reservedStorage: BigInteger,
		val storageCapabilityId : UUID
) : HostReservation, StorageReservation<Host> {
	init {
		check(reservedStorage > BigInteger.ZERO) {
			"reserved storage ($reservedStorage) must be bigger then 0"
		}
	}
	override fun isShared() = true
}