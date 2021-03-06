package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorageFactory
import com.github.kerubistan.kerub.utils.any
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmThinLv
import io.github.kerubistan.kroki.collections.concat
import kotlin.reflect.KClass

object CreateThinLvFactory : AbstractCreateVirtualStorageFactory<CreateThinLv>() {
	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<CreateThinLv> =
			factoryFeature(state.controllerConfig.storageTechnologies.lvmCreateVolumeEnabled) {
				val storageNotAllocated = state.index.virtualStorageNotAllocated

				state.index.runningHosts.filter {
					(it.config?.storageConfiguration?.any<LvmPoolConfiguration>() ?: false)
							&& LvmThinLv.available(it.stat.capabilities)
				}.mapNotNull { hostColl ->
					hostColl.config?.storageConfiguration?.filterIsInstance<LvmPoolConfiguration>()?.map { pool ->
						storageNotAllocated.map { disk ->
							CreateThinLv(
									disk = disk,
									poolName = pool.poolName,
									host = hostColl.stat,
									capability = requireNotNull(hostColl.stat.capabilities?.storageCapabilities
											?.filterIsInstance<LvmStorageCapability>()?.single { it.volumeGroupName == pool.vgName })
							)
						}
					}
				}.concat().concat()
			}
}