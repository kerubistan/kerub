package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.compareTo
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmVg
import io.github.kerubistan.kroki.numbers.times

class CreateLvmPoolExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostCfgDao: HostConfigurationDao
) : AbstractStepExecutor<CreateLvmPool, LogicalVolume>() {

	override fun prepare(step: CreateLvmPool) = hostCommandExecutor.execute(step.host) {
		LvmVg.list(session = it, vgName = step.vgName).single().let {
			val expectedFreeSpace = step.size * 1.01
			check(it.freeSize >= expectedFreeSpace) {
				"volume group ${step.vgName} on host ${step.host.address} is expected to have at least " +
						"$expectedFreeSpace but only have ${it.freeSize}"
			}
		}
	}

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