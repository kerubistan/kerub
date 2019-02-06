package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv

class CreateLvmPoolExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostCfgDao: HostConfigurationDao
) : AbstractStepExecutor<CreateLvmPool, LogicalVolume>() {
	override fun perform(step: CreateLvmPool) =
			hostCommandExecutor.execute(step.host) {
				LvmLv.createPool(it, step.vgName, step.name, step.size, step.size / 100.toBigInteger())
				LvmLv.list(it, step.vgName, step.name).single()
			}

	override fun update(step: CreateLvmPool, updates: LogicalVolume) {
		hostCfgDao.update(step.host.id,
				retrieve = { hostCfgDao[it] ?: HostConfiguration(id = step.host.id) },
				change = { hostCfg ->
					hostCfg.copy(
							storageConfiguration = hostCfg.storageConfiguration + LvmPoolConfiguration(
									vgName = step.vgName,
									size = updates.size,
									poolName = step.name
							)
					)
				}
		)
	}

}