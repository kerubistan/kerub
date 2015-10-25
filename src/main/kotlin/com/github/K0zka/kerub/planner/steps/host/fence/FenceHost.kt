package com.github.K0zka.kerub.planner.steps.host.fence

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

public class FenceHost(host : Host) : AbstractOperationalStep {
	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}