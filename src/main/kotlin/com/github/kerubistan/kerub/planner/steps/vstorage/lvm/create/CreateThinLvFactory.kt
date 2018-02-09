package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorageFactory
import com.github.kerubistan.kerub.utils.join

object CreateThinLvFactory : AbstractCreateVirtualStorageFactory<CreateThinLv>() {
	override fun produce(state: OperationalState): List<CreateThinLv> =
			factoryFeature(state.controllerConfig.storageTechnologies.lvmCreateVolumeEnabled) {
				val storageNotAllocated = listStorageNotAllocated(state)
				val runningHosts = listRunningHosts(state)

				runningHosts.filter {
					it.dynamic?.status == HostStatus.Up
							&& it.config?.storageConfiguration?.any { it is LvmPoolConfiguration } ?: false
				}.map { hostColl ->
							hostColl.config?.storageConfiguration?.filterIsInstance<LvmPoolConfiguration>()?.map { pool ->
								storageNotAllocated.map { disk ->
									CreateThinLv(
											disk = disk,
											poolName = pool.poolName,
											volumeGroupName = pool.vgName,
											host = hostColl.stat
									)
								}
							}
						}.filterNotNull().join().join()
			}
}