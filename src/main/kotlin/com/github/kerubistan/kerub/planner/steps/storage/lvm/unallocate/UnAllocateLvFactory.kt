package com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocateFactory
import kotlin.reflect.KClass

object UnAllocateLvFactory : AbstractUnAllocateFactory<UnAllocateLv, VirtualStorageLvmAllocation>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun unAllocate(
			allocation: VirtualStorageLvmAllocation,
			vStorage: VirtualStorageDataCollection, state: OperationalState
	) =
			UnAllocateLv(
					vstorage = vStorage.stat,
					host = requireNotNull(state.hosts[allocation.hostId]).stat,
					allocation = allocation
			)

	override val type: KClass<VirtualStorageLvmAllocation>
		get() = VirtualStorageLvmAllocation::class

}