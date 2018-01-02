package com.github.kerubistan.kerub.planner.violations.vm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.MemoryClockFrequencyExpectation
import com.github.kerubistan.kerub.planner.OperationalState

object MemoryClockFrequencyExpectationViolationDetector : AbstractVmHostViolationDetector<MemoryClockFrequencyExpectation>() {
	override fun check(entity: VirtualMachine,
					   expectation: MemoryClockFrequencyExpectation,
					   state: OperationalState,
					   host: Host): Boolean =
			host.capabilities?.memoryDevices?.let {
				it.isNotEmpty()
						&& it.all { it.speedMhz ?: 0 >= expectation.min }
			} ?: false
}