package com.github.kerubistan.kerub.planner.violations

import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.expectations.VirtualNetworkExpectation

/**
 * Interface for detectors of violations of virtual network expectations.
 */
interface VNetworkViolationDetector : ViolationDetector<VirtualNetwork, VirtualNetworkExpectation>