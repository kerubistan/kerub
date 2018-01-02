package com.github.kerubistan.kerub.planner.violations

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.VirtualMachineExpectation

/**
 * Interface for detectors of violations of Vm expectations.
 */
interface VmViolationDetector<in E : VirtualMachineExpectation> : ViolationDetector<VirtualMachine, E>