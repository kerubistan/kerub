package com.github.kerubistan.kerub.planner.violations.vm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.NotSameHostExpectation
import com.github.kerubistan.kerub.planner.OperationalState

object NotSameHostExpectationViolationDetector : AbstractVmHostViolationDetector<NotSameHostExpectation>() {
	override fun check(entity: VirtualMachine,
					   expectation: NotSameHostExpectation,
					   state: OperationalState,
					   host: Host): Boolean =
			state.vmHost(expectation.otherVmId)?.id != host.id
}