package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block

import com.github.kerubistan.kerub.model.VolumeManagerStorageCapability
import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolumeFactory
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate.UnAllocateGvinumFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLvFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateThinLvFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLvFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.AbstractMigrateAllocationFactory
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.now

object MigrateBlockAllocationFactory : AbstractMigrateAllocationFactory<MigrateBlockAllocation>() {

	override val allocationFactories = listOf(CreateLvFactory, CreateThinLvFactory, CreateGvinumVolumeFactory)

	private val deAllocationFactories = listOf(UnAllocateLvFactory, UnAllocateGvinumFactory)

	/*
	idea: take each RW virtual storage
		(RO could be just duplicated, so I don't care about that here)
	let the allocation factories generate the target allocations
		filter out the allocations where the disk is already actually allocated on the same host
		(for storage we have to tell the allocation factory that it is not yet allocated)
	then let the de-allocation factories produce offers for de-allocation
		(we have to tell these factories that the storage is to be recycled)
	then let's filter for the combinations where the source host's key
		is installed on the target host
	 */
	override fun produce(state: OperationalState): List<MigrateBlockAllocation> =
			state.vStorage.values.filter { canMigrate(it, state) }.map { candidateStorage ->
				val unAllocatedState = state.copy(
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
				val notOnOriginalHost: (AbstractCreateVirtualStorage<out VirtualStorageBlockDeviceAllocation, out VolumeManagerStorageCapability>) -> Boolean =
						{ step ->
							candidateStorage.dynamic?.allocations?.none { it.hostId == step.allocation.hostId }
									?: false
						}
				val allocationSteps =
						allocationFactories.map { it.produce(unAllocatedState) }.join().filter(notOnOriginalHost)


				allocationSteps.map { allocationStep ->
					val deAllocationSteps = deAllocationFactories.map {
						val unallocationState = state.copy(
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
						it.produce(unallocationState)
					}.join().filter { it.vstorage.id == candidateStorage.id }

					deAllocationSteps.map {
						MigrateBlockAllocation(
								deAllocationStep = it,
								allocationStep = allocationStep,
								virtualStorage = candidateStorage.stat,
								compression = null,
								sourceAllocation = candidateStorage.dynamic!!.allocations.single() as VirtualStorageBlockDeviceAllocation,
								sourceHost = it.host,
								targetHost = allocationStep.host
						)
					}

				}

			}.join().join().filter { it.targetHost in (state.connectionTargets[it.sourceHost.id] ?: listOf()) }

	private fun canMigrate(vstorage: VirtualStorageDataCollection, state: OperationalState): Boolean {
		val requiresVstorage: (VirtualMachineDataCollection) -> Boolean =
				{ vm -> vm.stat.virtualStorageLinks.any { link -> link.virtualStorageId == vstorage.id } }
		return (!vstorage.stat.readOnly
				&& !hasAnyAllocations(vstorage)
				&& state.runningVms.none(requiresVstorage)
				&& state.vmsThatMustStart.none(requiresVstorage))
	}


	private fun hasAnyAllocations(it: VirtualStorageDataCollection) =
			it.dynamic?.allocations?.isEmpty() ?: true
}