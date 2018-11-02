package com.github.kerubistan.kerub.planner.steps.vstorage.gvinum.unallocate

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.base.AbstractUnAllocateFactory
import kotlin.reflect.KClass

object UnAllocateGvinumFactory : AbstractUnAllocateFactory<UnAllocateGvinum, VirtualStorageGvinumAllocation>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun unAllocate(allocation: VirtualStorageGvinumAllocation, vStorage: VirtualStorageDataCollection, state: OperationalState) =
			UnAllocateGvinum(
					host = requireNotNull(state.hosts[allocation.hostId]).stat,
					allocation = allocation,
					vstorage = vStorage.stat
			)

	override val type: KClass<VirtualStorageGvinumAllocation>
		get() = VirtualStorageGvinumAllocation::class
}