package com.github.kerubistan.kerub.planner.steps.storage.lvm.mirror

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.expectations.StoragePerformanceExpectation
import com.github.kerubistan.kerub.model.expectations.StorageReadPerformanceExpectation
import com.github.kerubistan.kerub.model.expectations.StorageReadWritePerformance
import com.github.kerubistan.kerub.model.expectations.StorageRedundancyExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware.FailingStorageDevice
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.produceIf
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.mapInstances
import java.math.BigInteger
import kotlin.reflect.KClass

/**
 * Produces a MirrorVolume step for each disk's each LVM allocation
 * if the host is running and there is enough disk and free capacity on
 * the VG.
 */
@ExperimentalUnsignedTypes
object MirrorVolumeFactory : AbstractOperationalStepFactory<MirrorVolume>() {

	private val mirrorRange = (0.toUByte()..maxMirrors)

	override fun produce(state: OperationalState): List<MirrorVolume> =
			state.vStorage.values.mapNotNull { virtualStorage ->
				virtualStorage.dynamic?.allocations?.mapInstances<VirtualStorageLvmAllocation, List<MirrorVolume>> { allocation ->
					getCapabilityAvailable(allocation, state)?.let { freeCapacity ->
						val host = state.hosts.getValue(allocation.hostId)
						val capability = host.stat.capabilities?.storageCapabilities
								?.single { it.id == allocation.capabilityId } as LvmStorageCapability
						mirrorRange.mapNotNull { mirrors ->
							val change = mirrors.toByte() - allocation.mirrors
							when {
								change < 0 ->
									// we can always offer to remove mirrors
									// sometimes of course it is not a bright idea
									// but that's not the problem for the step factory
									MirrorVolume(
											mirrors = mirrors.toUShort(),
											vStorage = virtualStorage.stat,
											allocation = allocation,
											capability = capability,
											host = host.stat
									)
								change > 0 ->
									// we have to check if the size fits, there are enough PVs
									produceIf(capability.physicalVolumes.size > mirrors.toByte()
											&& freeCapacity > allocation.actualSize * mirrors.toInt().toBigInteger()) {
										MirrorVolume(
												mirrors = mirrors.toUShort(),
												vStorage = virtualStorage.stat,
												allocation = allocation,
												capability = capability,
												host = host.stat
										)
									}
								else ->
									// means no change in the number of mirrors, we can skip
									null
							}
						}
					}
				}
			}.join().join()

	private fun getCapabilityAvailable(allocation: VirtualStorageLvmAllocation, state: OperationalState): BigInteger? =
			state.hosts[allocation.hostId]?.let { host ->
				if (host.dynamic?.status == HostStatus.Up) {
					host.dynamic.storageStatus.singleOrNull { it.id == allocation.capabilityId }?.freeCapacity
				} else null
			}

	override val problemHints: Set<KClass<out Problem>>
		get() = setOf(FailingStorageDevice::class)
	override val expectationHints: Set<KClass<out Expectation>>
		get() = setOf(
				StorageReadPerformanceExpectation::class,
				StorageRedundancyExpectation::class,
				StoragePerformanceExpectation::class,
				StorageReadWritePerformance::class)
}