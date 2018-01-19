package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.VirtualMachineExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.violations.VmViolationDetector

abstract class AbstractVmHostViolationDetector<in E : VirtualMachineExpectation> : VmViolationDetector<E> {
	override fun check(
			entity: VirtualMachine,
			expectation: E,
			state: OperationalState): Boolean
			= state.vmHost(entity)?.let {
		checkWithHost(entity, expectation, state, it)
	} ?: true

	abstract internal fun checkWithHost(entity: VirtualMachine, expectation: E, state: OperationalState, host: Host): Boolean
}