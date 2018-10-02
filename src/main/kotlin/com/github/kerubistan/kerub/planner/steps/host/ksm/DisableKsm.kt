package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.ProducedBy
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep

@ProducedBy(KsmFactory::class)
data class DisableKsm(override val host: Host) : HostStep, InvertibleStep {
	override fun isInverseOf(other: AbstractOperationalStep) = other is EnableKsm && other.host == host

	override fun take(state: OperationalState): OperationalState = state
}