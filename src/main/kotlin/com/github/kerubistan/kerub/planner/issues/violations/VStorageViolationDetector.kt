package com.github.kerubistan.kerub.planner.issues.violations

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.expectations.VirtualStorageExpectation
import com.github.kerubistan.kerub.planner.OperationalState

/**
 * Interface for detectors of violations of virtual storage expectations.
 */
interface VStorageViolationDetector<in E : VirtualStorageExpectation> :
		ViolationDetector<VirtualStorageDevice, E, OperationalState>