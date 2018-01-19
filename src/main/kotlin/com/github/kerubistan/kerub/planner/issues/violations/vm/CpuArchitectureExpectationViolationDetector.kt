package com.github.kerubistan.kerub.planner.issues.violations.vm

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.CpuArchitectureExpectation
import com.github.kerubistan.kerub.planner.OperationalState

object CpuArchitectureExpectationViolationDetector : AbstractVmHostViolationDetector<CpuArchitectureExpectation>(){
	override fun checkWithHost(entity: VirtualMachine,
							   expectation: CpuArchitectureExpectation,
							   state: OperationalState,
							   host: Host): Boolean
	= expectation.cpuArchitecture == host.capabilities?.cpuArchitecture

}