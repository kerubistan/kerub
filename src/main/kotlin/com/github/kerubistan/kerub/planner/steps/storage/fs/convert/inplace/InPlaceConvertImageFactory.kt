package com.github.kerubistan.kerub.planner.steps.storage.fs.convert.inplace

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.produceIf
import io.github.kerubistan.kroki.collections.join
import kotlin.reflect.KClass

/**
 * Produces steps to convert disk in place (meaning result will be in same storage capability on the same host)
 * but it requires that the given allocation of the virtual storage is not used during this time.
 */
object InPlaceConvertImageFactory : AbstractOperationalStepFactory<InPlaceConvertImage>() {

	override fun produce(state: OperationalState) = state.index.allocatedStorage.mapNotNull { coll ->
		coll.dynamic?.allocations?.filterIsInstance<VirtualStorageFsAllocation>()?.mapNotNull { allocation ->
			produceIf(allocation.hostId in state.index.runningHostIds && !isUsed(coll.stat, state)) {
				(VirtualDiskFormat.values().filterNot { it == allocation.type }).map { format ->
					InPlaceConvertImage(
							virtualStorage = coll.stat,
							host = state.hosts.getValue(allocation.hostId).stat,
							sourceAllocation = allocation,
							targetFormat = format
					)
				}
			}
		}
	}.join().join()

	/*
	 * TODO: this checks if the virtual disk is used by any running VM
	 * which is right with RW disks, but not right with RO which can have many allocations
	 * relatively low-prio issue since once in use, the same format will do fine
	 */
	private fun isUsed(vdisk: VirtualStorageDevice, state: OperationalState) =
			state.index.runningVms.any { it.stat.virtualStorageLinks.any { it.virtualStorageId == vdisk.id } }

	override val problemHints: Set<KClass<out Problem>>
		get() = setOf()
	override val expectationHints: Set<KClass<out Expectation>>
		get() = setOf(StorageAvailabilityExpectation::class)
}