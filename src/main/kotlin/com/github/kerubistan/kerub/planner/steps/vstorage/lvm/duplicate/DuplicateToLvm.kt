package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.duplicate

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.steps.vstorage.block.duplicate.AbstractBlockDuplicate

data class DuplicateToLvm(
		override val vStorageDevice: VirtualStorageDevice,
		override val source: VirtualStorageBlockDeviceAllocation,
		override val sourceHost: Host,
		override val target: VirtualStorageLvmAllocation,
		override val targetHost: Host) : AbstractBlockDuplicate<VirtualStorageLvmAllocation>()