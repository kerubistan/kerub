package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.extend

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.reservations.HostStorageReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.common.AbstractPoolResizeStep
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.common.volumeGroupId
import java.math.BigInteger

data class ExtendLvmPool(
		override val host: Host,
		override val vgName: String,
		override val pool: String,
		val addSize: BigInteger) : AbstractPoolResizeStep() {

	override fun sizeChange(): BigInteger = addSize

	override fun reservations(): List<Reservation<*>> = listOf(
			UseHostReservation(host),
			HostStorageReservation(host, addSize, volumeGroupId(host.capabilities!!.storageCapabilities, vgName))
	)
}