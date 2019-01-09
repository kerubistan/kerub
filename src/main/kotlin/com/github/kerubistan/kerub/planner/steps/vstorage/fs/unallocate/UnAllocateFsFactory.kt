package com.github.kerubistan.kerub.planner.steps.vstorage.fs.unallocate

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocateFactory
import kotlin.reflect.KClass

object UnAllocateFsFactory : AbstractUnAllocateFactory<UnAllocateFs, VirtualStorageFsAllocation>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun unAllocate(
			allocation: VirtualStorageFsAllocation,
			vStorage: VirtualStorageDataCollection,
			state: OperationalState
	) =
			UnAllocateFs(
					vstorage = vStorage.stat,
					allocation = allocation,
					host = requireNotNull(state.hosts[allocation.hostId]) { "host ${allocation.hostId} not found" }.stat)

	override val type: KClass<VirtualStorageFsAllocation>
		get() = VirtualStorageFsAllocation::class
}