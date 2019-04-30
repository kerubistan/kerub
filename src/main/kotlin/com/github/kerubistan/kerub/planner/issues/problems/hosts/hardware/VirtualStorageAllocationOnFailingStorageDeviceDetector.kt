package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.model.VolumeManagerStorageCapability
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.utils.join

object VirtualStorageAllocationOnFailingStorageDeviceDetector
	: AbstractFailingStorageDeviceDetector<VirtualStorageAllocationOnFailingStorageDevice>() {
	override fun createProblems(host: HostDataCollection, failingDevice: String, plan: Plan) =
			host.stat.capabilities?.storageCapabilities?.filterIsInstance<VolumeManagerStorageCapability>()?.filter {
				failingDevice in it.storageDevices
			}?.map { failingCapability ->
				plan.state.index.allocatedStorage.mapNotNull { vdisk ->
					vdisk.dynamic?.allocations?.mapNotNull { allocation ->
						if (allocation.capabilityId == failingCapability.id) {
							VirtualStorageAllocationOnFailingStorageDevice(
									host = host.stat,
									capability = failingCapability,
									storageDevice = vdisk.stat,
									allocation = allocation
							)
						} else null
					}
				}
			}?.join()?.join()
}