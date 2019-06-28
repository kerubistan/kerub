package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.CacheSizeExpectation
import com.github.kerubistan.kerub.planner.OperationalState

object CacheSizeExpectationViolationDetector : AbstractVmHostViolationDetector<CacheSizeExpectation>() {
	override fun checkWithHost(
			entity: VirtualMachine, expectation: CacheSizeExpectation, state: OperationalState, host: Host
	): Boolean = expectation.minL1 <= host.capabilities?.cpus?.firstOrNull()?.l1cache?.size ?: 0

}