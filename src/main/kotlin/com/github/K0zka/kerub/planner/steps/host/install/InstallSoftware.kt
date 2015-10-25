package com.github.K0zka.kerub.planner.steps.host.install

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

public class InstallSoftware (packageName : String) : AbstractOperationalStep {
	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}