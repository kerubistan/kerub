package com.github.kerubistan.kerub.planner.steps.vstorage.fs.convert

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

abstract class AbstractConvertImage : AbstractOperationalStep {
	abstract val virtualStorage: VirtualStorageDevice
	abstract val fromAllocation : VirtualStorageAllocation
}