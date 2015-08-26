package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState
import com.github.k0zka.finder4j.backtrack.Step
import java.util.*

public class StartVirtualMachine : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}