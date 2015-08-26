package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState
import com.github.k0zka.finder4j.backtrack.Step

public class PowerDownHost : AbstractOperationalStep() {

	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}