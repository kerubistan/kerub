package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.remove

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.storage.lvm.base.updateHostDynLvmWithAllocation
import com.github.kerubistan.kerub.utils.update

data class RemoveLvmPool(
		val host: Host,
		val pool: String,
		val vgName: String
) : AbstractOperationalStep {
	override fun reservations(): List<Reservation<*>> = listOf(UseHostReservation(host))

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) { hostData ->
				val pool = hostData.config!!.storageConfiguration.single {
					it is LvmPoolConfiguration && it.poolName == pool
				} as LvmPoolConfiguration
				hostData.copy(
						config = requireNotNull(hostData.config).let { cfg ->
							cfg.copy(
									storageConfiguration = cfg.storageConfiguration.filterNot {
										it is LvmPoolConfiguration
												&& it.poolName == this.pool
												&& it.vgName == vgName
									}
							)
						},
						dynamic = updateHostDynLvmWithAllocation(state, host, vgName, pool.size.negate())
				)
			}
	)
}