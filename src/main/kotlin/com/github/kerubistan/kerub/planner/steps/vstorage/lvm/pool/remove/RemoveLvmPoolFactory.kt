package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.remove

import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.join

object RemoveLvmPoolFactory : AbstractOperationalStepFactory<RemoveLvmPool>() {
	override fun produce(state: OperationalState): List<RemoveLvmPool> {
		val usedPools = state.vStorage.values.map {
			it.dynamic?.allocations?.filterIsInstance(VirtualStorageLvmAllocation::class.java)
		}.filterNotNull().join().map { it.pool }.filterNotNull().distinct()

		val notUsedPools = state.hosts.mapNotNull { host ->
			host.value.config?.storageConfiguration?.filterIsInstance(LvmPoolConfiguration::class.java)
					?.filterNot { it.poolName in usedPools }?.map { host to it }
		}.join()

		return notUsedPools.map { RemoveLvmPool(pool = it.second.poolName, vgName = it.second.vgName, host = it.first.value.stat) }

	}
}