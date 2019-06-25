package com.github.kerubistan.kerub.planner.steps.storage.block.copy

import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage

abstract class AbstractBlockCopy  : AbstractOperationalStep {
	abstract val sourceDevice: VirtualStorageDevice
	abstract val targetDevice: VirtualStorageDevice
	abstract val sourceAllocation: VirtualStorageBlockDeviceAllocation
	abstract val allocationStep: AbstractCreateVirtualStorage<out VirtualStorageAllocation, out StorageCapability>

}