package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

public data class DisableKsm(val host: Host) : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState = state
}