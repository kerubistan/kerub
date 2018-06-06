package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorageFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.util.hasEnoughFreeCapacity

object CreateLvFactory : AbstractCreateVirtualStorageFactory<CreateLv>() {

	override fun produce(state: OperationalState): List<CreateLv> =
			factoryFeature(state.controllerConfig.storageTechnologies.lvmCreateVolumeEnabled) {
				val storageNotAllocated = listStorageNotAllocated(state)
				val runningHosts = listRunningHosts(state)

				var steps = listOf<CreateLv>()

				runningHosts.forEach { host ->
					host.stat.capabilities?.storageCapabilities?.filterIsInstance<LvmStorageCapability>()
							?.forEach { volGroup ->
								steps += storageNotAllocated.filter {
									hasEnoughFreeCapacity(volGroup, it, host.dynamic)
								}.map { disk ->
									CreateLv(
											host = host.stat,
											volumeGroupName = (volGroup as LvmStorageCapability).volumeGroupName,
											disk = disk
									)
								}
							}
				}
				steps
			}

}