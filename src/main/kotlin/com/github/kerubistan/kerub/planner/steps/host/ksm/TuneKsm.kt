package com.github.kerubistan.kerub.planner.steps.host.ksm

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.InvertibleStep
import com.github.kerubistan.kerub.planner.steps.ProducedBy
import com.github.kerubistan.kerub.planner.steps.vm.base.HostStep

@ProducedBy(KsmFactory::class)
@JsonTypeName("tune-ksm")
data class TuneKsm(override val host: Host) : HostStep, InvertibleStep {
	override fun take(state: OperationalState): OperationalState {
		TODO()
	}

	override fun reservations(): List<Reservation<*>> {
		TODO()
	}

	override fun isInverseOf(other: AbstractOperationalStep): Boolean {
		TODO()
	}
}