package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorageFactory
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass

object CreateThinLvFactory : AbstractCreateVirtualStorageFactory<CreateThinLv>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<CreateThinLv> =
			factoryFeature(state.controllerConfig.storageTechnologies.lvmCreateVolumeEnabled) {
				val storageNotAllocated = listStorageNotAllocated(state)

				state.runningHosts.filter {
							it.config?.storageConfiguration?.any { it is LvmPoolConfiguration } ?: false
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