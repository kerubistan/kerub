package com.github.kerubistan.kerub.planner.issues.violations

import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.expectations.VirtualNetworkExpectation
import com.github.kerubistan.kerub.planner.OperationalState

/**
 * Interface for detectors of violations of virtual network expectations.
 */
interface VNetworkViolationDetector : ViolationDetector<VirtualNetwork, VirtualNetworkExpectation, OperationalState>