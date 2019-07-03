package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.othercap

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.CreateDiskFactory
import com.github.kerubistan.kerub.utils.junix.qemu.QemuImg
import com.github.kerubistan.kerub.utils.update
import io.github.kerubistan.kroki.collections.join
import kotlin.reflect.KClass

object ConvertImageFactory : AbstractOperationalStepFactory<ConvertImage>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<ConvertImage> {
		val runningHosts = state.index.runningHosts
				.filter { QemuImg.available(it.stat.capabilities) }
				.map { it.stat.id }
		val allocationsOnRunningHosts = state.vStorage.mapNotNull { (_, storage) ->
			storage.dynamic?.allocations?.filter { it.hostId in runningHosts }?.let { availableAllocations ->
				storage.stat to availableAllocations
			}
		}

		return allocationsOnRunningHosts.map { (storage, allocations) ->
			val unAllocatedState = state.copy(
					vStorage = state.vStorage.update(storage.id) { storageColl ->
						storageColl.copy(
								stat = storageColl.stat.copy(
										expectations = storageColl.stat.expectations + StorageAvailabilityExpectation()
								),
								dynamic = null
						)
					}
			)
			allocations.map { allocation ->
				val host = requireNotNull(state.hosts[allocation.hostId])
				// TODO: this may be high-cost operation if there are tons os hosts
				// maybe it would be better to narrow down the state of the cluster to the host
				val targetAllocationSteps = CreateDiskFactory.produce(unAllocatedState)
						// they all should be
						.filterIsInstance<AbstractCreateVirtualStorage<*, *>>()
						.filter { it.host.id == host.stat.id }

				targetAllocationSteps.map {
					ConvertImage(
							host = requireNotNull(state.hosts[allocation.hostId]).stat,
							virtualStorage = storage,
							sourceAllocation = allocation,
							targetAllocation = it.allocation
					)
				}

			}
		}.join().join()
	}
}