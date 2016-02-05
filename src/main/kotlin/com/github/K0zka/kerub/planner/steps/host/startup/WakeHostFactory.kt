package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

/**
 * Generates steps to wake up non-running hosts.
 */
public object WakeHostFactory : AbstractOperationalStepFactory<WakeHost>() {
	override fun produce(state: OperationalState): List<WakeHost> {
		return state.hosts.filter {
			val dyn = state.hostDyns[it.key]
			dyn == null || dyn.status == HostStatus.Down
		}.map { WakeHost(it.value) }
	}
}