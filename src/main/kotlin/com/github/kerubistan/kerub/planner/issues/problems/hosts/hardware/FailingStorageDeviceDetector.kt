package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.model.VolumeManagerStorageCapability
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.utils.join

object FailingStorageDeviceDetector : ProblemDetector<FailingStorageDevice> {
	override fun detect(plan: Plan) = plan.state.hosts.values.mapNotNull { host: HostDataCollection ->
		host.dynamic?.storageDeviceHealth?.mapNotNull { (device, healthy) ->
			if (healthy) null else
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
	}.join().join()
}