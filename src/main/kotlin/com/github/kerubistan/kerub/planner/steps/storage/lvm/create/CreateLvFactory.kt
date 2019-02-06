package com.github.kerubistan.kerub.planner.steps.storage.lvm.create

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorageFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.util.hasEnoughFreeCapacity
import kotlin.reflect.KClass

object CreateLvFactory : AbstractCreateVirtualStorageFactory<CreateLv>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<CreateLv> =
			factoryFeature(state.controllerConfig.storageTechnologies.lvmCreateVolumeEnabled) {
				val storageNotAllocated = listStorageNotAllocated(state)
				var steps = listOf<CreateLv>()

				state.runningHosts.forEach { host ->
					host.stat.capabilities?.storageCapabilities?.filterIsInstance<LvmStorageCapability>()
							?.filter {
								it.volumeGroupName.matches(state.controllerConfig.storageTechnologies.lvmVgPatternRegex)
							}
							?.forEach { volGroup ->
								steps += storageNotAllocated.filter {
									hasEnoughFreeCapacity(volGroup, it, host.dynamic)
								}.map { disk ->
									CreateLv(
											host = host.stat,
											disk = disk,
											capability = volGroup
									)
								}
							}
				}
				steps
			}

}