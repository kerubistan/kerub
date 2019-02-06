package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.shrink

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.common.AbstractPoolResizeStep
import java.math.BigInteger

data class ShrinkLvmPool(
		override val host: Host,
		override val vgName: String,
		override val pool: String,
		val shrinkBy: BigInteger
) : AbstractPoolResizeStep() {

	override fun sizeChange(): BigInteger = shrinkBy.negate()

	override fun reservations(): List<Reservation<*>> = listOf(
			UseHostReservation(host)
	)
}