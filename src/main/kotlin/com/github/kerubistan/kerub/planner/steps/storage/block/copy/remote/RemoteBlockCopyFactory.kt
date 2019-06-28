package com.github.kerubistan.kerub.planner.steps.storage.block.copy.remote

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.block.copy.AbstractBlockCopyFactory
import com.github.kerubistan.kerub.utils.contains
import io.github.kerubistan.kroki.collections.join

object RemoteBlockCopyFactory : AbstractBlockCopyFactory<RemoteBlockCopy>() {
	override fun produce(state: OperationalState): List<RemoteBlockCopy> =
			state.index.storageCloneRequirement.map { targetStorage ->

				val cloneOfStorageExpectation =
						targetStorage.stat.expectations.filterIsInstance<CloneOfStorageExpectation>().single()
				val sourceStorage = state.vStorage.getValue(cloneOfStorageExpectation.sourceStorageId)
				val unallocatedState = createUnallocatedState(state, targetStorage)

				allocationFactories.map { factory ->
					factory.produce(unallocatedState).map { allocationStep ->

						sourceStorage.dynamic?.allocations?.mapNotNull { sourceAllocation ->
							val sourceHost = state.hosts.getValue(sourceAllocation.hostId)
							if (sourceAllocation is VirtualStorageBlockDeviceAllocation &&
									allocationStep.host in state.index.connectionTargets[sourceHost.id]) {
								RemoteBlockCopy(
										sourceHost = sourceHost.stat,
										targetDevice = targetStorage.stat,
										allocationStep = allocationStep,
										sourceDevice = sourceStorage.stat,
										sourceAllocation = sourceAllocation
								)
							} else null
						}

					}
				}
			}.join().join().filterNotNull().join()
}