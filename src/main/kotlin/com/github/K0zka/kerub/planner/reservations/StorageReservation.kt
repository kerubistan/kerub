package com.github.K0zka.kerub.planner.reservations

import com.github.K0zka.kerub.model.Entity
import java.math.BigInteger

interface StorageReservation<T : Entity<*>> : Reservation<T> {
	val reservedStorage: BigInteger
}