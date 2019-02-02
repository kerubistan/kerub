package com.github.kerubistan.kerub.planner.steps.vstorage.migrate.dead

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHost
import com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware.FailingStorageDevice
import com.github.kerubistan.kerub.planner.issues.problems.vstorage.VStorageDeviceOnRecyclingHost
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage
import kotlin.reflect.KClass

abstract class AbstractMigrateAllocationFactory<out T : AbstractMigrateAllocation> : AbstractOperationalStepFactory<T>() {

	abstract val allocationFactories: List<AbstractOperationalStepFactory<AbstractCreateVirtualStorage<out VirtualStorageAllocation, out StorageCapability>>>


	override val problemHints: Set<KClass<out Problem>>
		get() = setOf(FailingStorageDevice::class, RecyclingHost::class, VStorageDeviceOnRecyclingHost::class)
	override val expectationHints: Set<KClass<out Expectation>>
		get() = setOf()
}