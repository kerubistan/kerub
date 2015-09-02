package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState

public class PowerDownHost(val host : Host) : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}