package com.github.kerubistan.kerub.planner.violations

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.expectations.VirtualStorageExpectation

/**
 * Interface for detectors of violations of virtual storage expectations.
 */
interface VStorageViolationDetector : ViolationDetector<VirtualStorageDevice, VirtualStorageExpectation>