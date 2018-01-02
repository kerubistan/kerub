package com.github.kerubistan.kerub.planner.violations.vm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.ClockFrequencyExpectation
import com.github.kerubistan.kerub.planner.OperationalState

object ClockFrequencyExpectationViolationDetector : AbstractVmHostViolationDetector<ClockFrequencyExpectation>() {
	override fun check(
			entity: VirtualMachine,
			expectation: ClockFrequencyExpectation,
			state: OperationalState,
			host: Host): Boolean
			= host.capabilities?.cpus?.firstOrNull()?.maxSpeedMhz ?: 0 >= expectation.minimalClockFrequency

}