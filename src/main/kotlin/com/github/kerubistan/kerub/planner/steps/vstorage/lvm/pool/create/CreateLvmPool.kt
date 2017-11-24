package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.create

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.HostStorageReservation
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.base.updateHostDynLvmWithAllocation
import com.github.kerubistan.kerub.utils.update
import java.math.BigInteger

class CreateLvmPool(
		val host: Host,
		val vgName: String,
		val name: String,
		val size: BigInteger) : AbstractOperationalStep {
	override fun reservations(): List<Reservation<*>> = listOf(
			UseHostReservation(host), HostStorageReservation(host, size)
	)

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id, {
				it.copy(
						config = (it.config ?: HostConfiguration()).let { config ->
							config.copy(
									storageConfiguration = config.storageConfiguration +
											LvmPoolConfiguration(
													vgName = this.vgName,
													size = this.size,
													poolName = this.name
											)
							)
						},
						dynamic = updateHostDynLvmWithAllocation(state, host, vgName, size)
				)
			})
	)
}