package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHost
import com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware.FailingStorageDevice
import com.github.kerubistan.kerub.planner.issues.problems.vstorage.VStorageDeviceOnRecyclingHost
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import io.github.kerubistan.kroki.time.now
import kotlin.reflect.KClass

abstract class AbstractMigrateAllocationFactory<out T : AbstractMigrateAllocation> :
		AbstractOperationalStepFactory<T>() {

	abstract val allocationFactories: List<AbstractOperationalStepFactory<AbstractCreateVirtualStorage<out VirtualStorageAllocation, out StorageCapability>>>

	internal fun listMigrateableVirtualDisks(state: OperationalState) =
			state.vStorage.values.filter { canMigrate(it, state) }

	internal fun generteUnallocationState(
			state: OperationalState,
			candidateStorage: VirtualStorageDataCollection
	): OperationalState = state.copy(
			vStorage = state.vStorage + (candidateStorage.id to candidateStorage.copy(
					stat = candidateStorage.stat.copy(
							expectations = listOf(),
							recycling = true
					),
					dynamic = VirtualStorageDeviceDynamic(
							id = candidateStorage.id,
							allocations = candidateStorage.dynamic!!.allocations, // there is only one anyway, as this is RW for sure
							lastUpdated = now()
					)
			))
	)

	internal fun unallocatedState(
			state: OperationalState,
			candidateStorage: VirtualStorageDataCollection
	): OperationalState = state.copy(
			vStorage = state.vStorage + (candidateStorage.id to candidateStorage.copy(
					stat = candidateStorage.stat.copy(
							expectations = listOf(
									// 'raw' is specific to block allocation
									StorageAvailabilityExpectation()
							)
					),
					dynamic = VirtualStorageDeviceDynamic(
							id = candidateStorage.id,
							allocations = listOf(), // there is only one anyway, as this is RW for sure
							lastUpdated = now()
					)
			))
	)

	internal fun isSslKeyInstalled(
			it: AbstractMigrateAllocation,
			state: OperationalState
	) = it.targetHost in (state.index.connectionTargets[it.sourceHost.id] ?: listOf())

	private fun hasAnyAllocations(it: VirtualStorageDataCollection) =
			it.dynamic?.allocations?.isEmpty() ?: true

	private fun canMigrate(vstorage: VirtualStorageDataCollection, state: OperationalState): Boolean {
		val requiresVstorage: (VirtualMachineDataCollection) -> Boolean =
				{ vm -> vm.stat.virtualStorageLinks.any { link -> link.virtualStorageId == vstorage.id } }
		return (!vstorage.stat.readOnly
				&& !hasAnyAllocations(vstorage)
				&& state.index.runningVms.none(requiresVstorage)
				&& state.index.vmsThatMustStart.none(requiresVstorage))
	}


	override val problemHints: Set<KClass<out Problem>>
		get() = setOf(FailingStorageDevice::class, RecyclingHost::class, VStorageDeviceOnRecyclingHost::class)
	override val expectationHints: Set<KClass<out Expectation>>
		get() = setOf()
}