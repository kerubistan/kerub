package com.github.kerubistan.kerub.planner.reservations

import com.github.kerubistan.kerub.model.Entity
import java.io.Serializable

/**
 * Reservation describes the fact that a resource is to be used by a plan
 * and this fact needs to be considered by other planning processes against
 * the same resource.
 */
interface Reservation<T : Entity<*>> : Serializable {

	/**
	 * A reservation is shared if the planned operation does not interfere
	 * with other operations.
	 */
	fun isShared(): Boolean

	/**
	 * The entity reserved for an operation.
	 */
	fun entity(): T
}
