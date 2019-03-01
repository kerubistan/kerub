package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.sum
import java.math.BigInteger
import kotlin.reflect.KClass

object KsmFactory : AbstractOperationalStepFactory<AbstractOperationalStep>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	private fun totalMemoryUsedByVms(state: OperationalState, host: Host): BigInteger =
			state.vms.values.mapNotNull { if (it.dynamic?.hostId == host.id) it.dynamic.memoryUsed else null }
					.sum()

	override fun produce(state: OperationalState): List<AbstractOperationalStep> {
		return state.hosts.values.mapNotNull { host ->
			host.dynamic?.let { dyn ->
				if (dyn.ksmEnabled) {
					DisableKsm(host.stat)
				} else {
					EnableKsm(host.stat, totalMemoryUsedByVms(state, host.stat).toLong())
				}
			}
		}
	}
}