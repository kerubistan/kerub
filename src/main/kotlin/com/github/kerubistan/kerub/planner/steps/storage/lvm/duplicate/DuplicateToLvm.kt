package com.github.kerubistan.kerub.planner.steps.storage.lvm.duplicate

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.steps.storage.block.duplicate.AbstractBlockDuplicate

@JsonTypeName("duplicate-to-lvm")
data class DuplicateToLvm(
		override val virtualStorageDevice: VirtualStorageDevice,
		override val source: VirtualStorageBlockDeviceAllocation,
		override val sourceHost: Host,
		override val target: VirtualStorageLvmAllocation,
		override val targetHost: Host) : AbstractBlockDuplicate<VirtualStorageLvmAllocation>() {
	@get:JsonIgnore
	override val targetCapability: StorageCapability
		get() = requireNotNull(targetHost.capabilities).storageCapabilities
				.single { it is LvmStorageCapability && target.vgName == it.volumeGroupName }
}