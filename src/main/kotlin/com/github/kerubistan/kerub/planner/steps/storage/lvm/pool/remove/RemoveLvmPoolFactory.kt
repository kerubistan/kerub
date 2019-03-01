package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.remove

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass

object RemoveLvmPoolFactory : AbstractOperationalStepFactory<RemoveLvmPool>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<RemoveLvmPool> {
		val usedPools = state.vStorage.values.mapNotNull {
			it.dynamic?.allocations?.filterIsInstance(VirtualStorageLvmAllocation::class.java)
		}.join().mapNotNull { it.pool }.distinct()

		val notUsedPools = state.hosts.mapNotNull { host ->
			host.value.config?.storageConfiguration?.filterIsInstance(LvmPoolConfiguration::class.java)
					?.filterNot { it.poolName in usedPools }?.map { host to it }
		}.join()

		return notUsedPools.map { RemoveLvmPool(pool = it.second.poolName, vgName = it.second.vgName, host = it.first.value.stat) }

	}
}