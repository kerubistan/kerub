package com.github.kerubistan.kerub.planner.steps.vstorage.fs.unallocate

import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocateFactory
import kotlin.reflect.KClass

object UnAllocateFsFactory : AbstractUnAllocateFactory<UnAllocateFs, VirtualStorageFsAllocation>() {
	override fun unAllocate(allocation: VirtualStorageFsAllocation, vStorage: VirtualStorageDataCollection, state: OperationalState) =
			UnAllocateFs(vStorage.stat, allocation, requireNotNull(state.hosts[allocation.hostId]) { "host ${allocation.hostId} not found" }.stat)

	override val type: KClass<VirtualStorageFsAllocation>
		get() = VirtualStorageFsAllocation::class
}