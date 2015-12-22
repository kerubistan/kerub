package com.github.K0zka.kerub.planner.reservations

import java.math.BigInteger

interface StorageReservation : Reservation {
	val reservedStorage: BigInteger
}