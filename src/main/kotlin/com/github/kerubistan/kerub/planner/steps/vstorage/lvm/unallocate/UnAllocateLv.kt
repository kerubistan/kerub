package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.unallocate

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.base.UnAllocate

data class UnAllocateLv(override val vstorage: VirtualStorageDevice,
				   override val allocation: VirtualStorageLvmAllocation,
				   override val host: Host) : UnAllocate<VirtualStorageLvmAllocation>()