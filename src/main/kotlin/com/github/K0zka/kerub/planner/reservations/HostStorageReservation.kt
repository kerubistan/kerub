package com.github.K0zka.kerub.planner.reservations

import com.github.K0zka.kerub.model.Host
import java.math.BigInteger

data class HostStorageReservation(
		override val host: Host,
		override val reservedStorage: BigInteger
) : HostReservation, StorageReservation