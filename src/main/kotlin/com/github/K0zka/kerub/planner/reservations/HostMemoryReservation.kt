package com.github.K0zka.kerub.planner.reservations

import com.github.K0zka.kerub.model.Host
import java.math.BigInteger

data class HostMemoryReservation(
		override val host: Host,
		override val reservedStorage: BigInteger
) : HostReservation, StorageReservation