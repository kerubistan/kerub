package com.github.kerubistan.kerub.planner.issues.problems.storage

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import io.github.kerubistan.kroki.collections.concat
import io.github.kerubistan.kroki.numbers.compareTo
import io.github.kerubistan.kroki.numbers.sumBy
import io.github.kerubistan.kroki.numbers.times

/**
 * An problem detector checking LVM pools and reporting all over-allocated pools that could fill up and threaten service
 * level.
 */
object LvmPoolAlmostFullDetector : ProblemDetector<LvmPoolAlmostFull> {
	override fun detect(plan: Plan): Collection<LvmPoolAlmostFull> = plan.state.hosts.values.mapNotNull { host ->
		host.config?.storageConfiguration?.filterIsInstance<LvmPoolConfiguration>()?.mapNotNull { pool ->
			val lvmCap = getLvmCapability(host, pool)
			val lvmDyn = getLvmDyn(host, lvmCap)
			val poolDyn = lvmDyn.pools.firstOrNull { it.name == pool.poolName }
			if (poolDyn != null
					// over 90 percent full - there certainly is a more useful approach to this than a hardcoded
					// constant, but this is simple enough for now
					&& poolDyn.freeSize < poolDyn.size * 0.9
					&& hasOverAllocation(pool, lvmCap, plan.state)) {
				LvmPoolAlmostFull(
						pool = pool,
						freeSpace = poolDyn.freeSize,
						host = host.stat
				)
			} else null
		}
	}.concat()

	private fun getLvmDyn(host: HostDataCollection, lvmCap: LvmStorageCapability) =
			host.dynamic?.storageStatus
					?.firstOrNull { it is CompositeStorageDeviceDynamic && it.id == lvmCap.id } as CompositeStorageDeviceDynamic

	private fun getLvmCapability(host: HostDataCollection, pool: LvmPoolConfiguration): LvmStorageCapability {
		return host.stat.capabilities?.storageCapabilities?.single {
			it is LvmStorageCapability && it.volumeGroupName == pool.vgName
		} as LvmStorageCapability
	}

	/**
	 * A pool has over-allocation if the sum of the maximum size of the disks allocated on the pool
	 */
	private fun hasOverAllocation(
			pool: LvmPoolConfiguration,
			lvmCap: LvmStorageCapability,
			state: OperationalState): Boolean {

		val diskIds = state.index.allocatedStoragePerCapability[lvmCap.id] ?: setOf()
		val disks = diskIds.mapNotNull { state.vStorage[it] }
		val totalMax = disks.sumBy { it.stat.size }

		return totalMax > pool.size
	}
}