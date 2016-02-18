package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.FsStorageCapability
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object CreateImageFactory : AbstractVStorageCreateFactory<CreateImage>() {
	override fun produce(state: OperationalState): List<CreateImage> {
		val storageNotAllocated = listStorageNotAllocated(state)
		val runningHosts = listRunningHosts(state)

		var steps = listOf<CreateImage>()

		runningHosts.forEach {
			host ->
			host.capabilities?.storageCapabilities?.filter {
				capability
				->
				capability is FsStorageCapability && capability.mountPoint.startsWith("/var")
			}?.forEach {
				mount ->
				steps += storageNotAllocated.map {
					storage ->
					CreateImage(
							device = storage,
							host = host,
							format = VirtualDiskFormat.qcow2,
							path = (mount as FsStorageCapability).mountPoint
					)
				}
			}
		}

		return steps
	}


}