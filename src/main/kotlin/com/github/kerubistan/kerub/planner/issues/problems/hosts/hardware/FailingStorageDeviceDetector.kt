package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.model.VolumeManagerStorageCapability
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.planner.Plan

object FailingStorageDeviceDetector : AbstractFailingStorageDeviceDetector<FailingStorageDevice>() {

	override fun createProblems(host: HostDataCollection, device: String, plan: Plan): List<FailingStorageDevice>? =
		host.stat.capabilities?.storageCapabilities?.filterIsInstance<VolumeManagerStorageCapability>()?.filter {
			device in it.storageDevices
		}?.map {
			FailingStorageDevice(
					device = device,
					storageCapability = it,
					host = host.stat
			)
		}
}