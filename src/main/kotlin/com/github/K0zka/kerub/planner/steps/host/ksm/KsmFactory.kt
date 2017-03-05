package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.utils.sum
import java.math.BigInteger

object KsmFactory : AbstractOperationalStepFactory<AbstractOperationalStep>() {

	private fun totalMemoryUsedByVms(state: OperationalState, host: Host): BigInteger =
			state.vms.values.map { if (it.dynamic?.hostId == host.id) it.dynamic.memoryUsed else null }
					.filterNotNull()
					.sum()

	override fun produce(state: OperationalState): List<AbstractOperationalStep> {
		return state.hosts.values.map {
			host ->
			val dyn = host.dynamic
			if (dyn == null) {
				null
			} else
				if (dyn.ksmEnabled) {
					DisableKsm(host.stat)
				} else {
					EnableKsm(host.stat, totalMemoryUsedByVms(state, host.stat).toLong())
				}
		}.filterNotNull()
	}
}