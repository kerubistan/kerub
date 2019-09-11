package com.github.kerubistan.kerub.planner.steps.storage.lvm.mirror

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.reservations.HostStorageReservation
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.update
import java.math.BigInteger

@ExperimentalUnsignedTypes
data class MirrorVolume @ExperimentalUnsignedTypes constructor(
		val host: Host,
		val capability: LvmStorageCapability,
		val vStorage: VirtualStorageDevice,
		val allocation: VirtualStorageLvmAllocation,
		val mirrors: UShort
) : AbstractOperationalStep {

	init {
		check(
				host.capabilities?.storageCapabilities != null
						&& capability in host.capabilities.storageCapabilities) {
			"capability must be registered in the host"
		}
		check(mirrors < maxMirrors) {
			"$mirrors nr of copies How does it make sense to have that many mirrors?"
		}
		check(mirrors < capability.physicalVolumes.size.toUShort()) {
			"requested $mirrors mirrors, but only have ${capability.physicalVolumes.size} " +
					"in the vg ${capability.volumeGroupName}"
		}
	}

	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) { hostColl ->
				hostColl.copy(
						dynamic = requireNotNull(hostColl.dynamic).copy(
								storageStatus = hostColl.dynamic.storageStatus.update(
										selector = { it.id == capability.id },
										map = {
											it as CompositeStorageDeviceDynamic
											it.copy(
													reportedFreeCapacity = (it.freeCapacity - extraSpaceNeeded())
															.coerceAtLeast(BigInteger.ZERO)
											)
										})
						)
				)
			},
			vStorage = state.vStorage.update(vStorage.id) { vStorageColl ->
				vStorageColl.copy(
						dynamic = vStorageColl.dynamic!!.copy(
								allocations = vStorageColl.dynamic.allocations.update(
										selector = { it is VirtualStorageLvmAllocation && it.capabilityId == capability.id },
										map = { (it as VirtualStorageLvmAllocation).copy(mirrors = mirrors.toByte()) }
								)
						)
				)
			}
	)

	override fun reservations() =
			listOf(
					UseHostReservation(host),
					HostStorageReservation(
							host = host,
							reservedStorage = extraSpaceNeeded(),
							storageCapabilityId = capability.id
					)
			)

	private fun extraSpaceNeeded() = vStorage.size * (mirrors.toInt() - allocation.mirrors.toInt()).toBigInteger()
}