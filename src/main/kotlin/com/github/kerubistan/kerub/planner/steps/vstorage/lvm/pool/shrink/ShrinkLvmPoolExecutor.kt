package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.shrink

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import java.math.BigInteger

class ShrinkLvmPoolExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostCfgDao: HostConfigurationDao,
		private val hostDynamicDao: HostDynamicDao
) : AbstractStepExecutor<ShrinkLvmPool, BigInteger>() {
	override fun perform(step: ShrinkLvmPool): BigInteger {
		TODO("This functionality is not implemented in LVM2")
		// for quite some time... maybe never will
		// so the only way of actually shrinking a pool is to move
		// everything out of it and then delete it - super-expensive
	}

	override fun update(step: ShrinkLvmPool, updates: BigInteger) {
		TODO("This functionality is not implemented in LVM2")
	}
}