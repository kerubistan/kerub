package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.utils.sum
import java.math.BigInteger

object KsmFactory : AbstractOperationalStepFactory<AbstractOperationalStep>() {

	private fun totalMemoryUsedByVms(state: OperationalState, host: Host): BigInteger =
			state.vmDyns.values.map { if (it.hostId == host.id) it.memoryUsed else BigInteger.ZERO }.sum()

	override fun produce(state: OperationalState): List<AbstractOperationalStep> {
		return state.hosts.values.map {
			host ->
			val dyn = state.hostDyns[host.id]
			if (dyn == null) {
				null
			} else
				if (dyn.ksmEnabled) {
					DisableKsm(host)
				} else {
					EnableKsm(host, totalMemoryUsedByVms(state, host).toLong())
				}
		}.filterNotNull()
	}
}