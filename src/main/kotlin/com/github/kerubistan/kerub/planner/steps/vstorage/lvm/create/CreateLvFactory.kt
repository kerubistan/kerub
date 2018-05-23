package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorageFactory
import java.math.BigInteger

object CreateLvFactory : AbstractCreateVirtualStorageFactory<CreateLv>() {

	override fun produce(state: OperationalState): List<CreateLv> =
			factoryFeature(state.controllerConfig.storageTechnologies.lvmCreateVolumeEnabled) {
				val storageNotAllocated = listStorageNotAllocated(state)
				val runningHosts = listRunningHosts(state)

				var steps = listOf<CreateLv>()

				runningHosts.forEach { host ->
					host.stat.capabilities?.storageCapabilities?.filter { capability
						->
						capability is LvmStorageCapability
					}?.forEach { volGroup ->
						steps += storageNotAllocated.filter {
							volGroup.size > it.size
									&& actualFreeCapacity(host, volGroup) > it.size
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

	private fun actualFreeCapacity(host: HostDataCollection, volGroup: StorageCapability) = host.dynamic?.storageStatus?.firstOrNull { it.id == volGroup.id }?.freeCapacity
			?: BigInteger.ZERO

}