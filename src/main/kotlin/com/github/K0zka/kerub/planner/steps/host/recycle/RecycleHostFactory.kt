package com.github.K0zka.kerub.planner.steps.host.recycle

import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object RecycleHostFactory : AbstractOperationalStepFactory<RecycleHost>() {
	override fun produce(state: OperationalState): List<RecycleHost> =
			state.hosts.values.filter {
				(stat, dyn) ->
				//the host is being recycled
				stat.recycling &&
						//no more disk allocations on it
						state.vStorage.values.none {
							it.dynamic?.allocations?.any {
								it.hostId == stat.id
							} ?: false
						} &&
						//no vms running on it
						state.vms.values.none {
							it.dynamic?.hostId == stat.id
						} &&
						//it is either dedicated and shut down, or not dedicated
						((stat.dedicated && (dyn == null || dyn.status == HostStatus.Down))
								|| !stat.dedicated)
			}.map {
				RecycleHost(it.stat)
			}
}