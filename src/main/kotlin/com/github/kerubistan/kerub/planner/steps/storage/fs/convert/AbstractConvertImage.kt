package com.github.kerubistan.kerub.planner.steps.storage.fs.convert

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

abstract class AbstractConvertImage : AbstractOperationalStep {
	abstract val virtualStorage: VirtualStorageDevice
	abstract val sourceAllocation : VirtualStorageAllocation
}