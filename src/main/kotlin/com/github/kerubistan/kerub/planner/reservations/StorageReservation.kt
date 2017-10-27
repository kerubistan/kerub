package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.Entity
import java.math.BigInteger

interface StorageReservation<T : Entity<*>> : Reservation<T> {
	val reservedStorage: BigInteger
}