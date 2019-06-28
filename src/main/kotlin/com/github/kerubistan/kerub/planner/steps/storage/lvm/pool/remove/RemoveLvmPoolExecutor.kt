package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.remove

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmVg
import java.math.BigInteger

class RemoveLvmPoolExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostCfgDao: HostConfigurationDao,
		private val hostDynamicDao: HostDynamicDao
) : AbstractStepExecutor<RemoveLvmPool, BigInteger>() {

	override fun perform(step: RemoveLvmPool): BigInteger =
			hostCommandExecutor.execute(step.host) { session ->
				LvmLv.remove(session, step.vgName, step.pool)
				LvmVg.list(session, step.vgName).single().freeSize
			}

	override fun update(step: RemoveLvmPool, updates: BigInteger) {
		val storageCapability = requireNotNull(step.host.capabilities?.storageCapabilities?.single {
			it is LvmStorageCapability
					&& it.volumeGroupName == step.vgName
		})
		hostCfgDao.update(step.host.id) {
			it.copy(
					storageConfiguration = it.storageConfiguration.filterNot { cfg ->
						cfg is LvmPoolConfiguration
								&& cfg.poolName == step.pool
								&& cfg.vgName == step.vgName
					}
			)
		}

		hostDynamicDao.update(step.host.id) { dyn ->
			dyn.copy(
					storageStatus = dyn.storageStatus.map { status ->
						if (status.id == storageCapability.id) {
							(status as SimpleStorageDeviceDynamic).copy(freeCapacity = updates)
						} else {
							status
						}
					}
			)
		}
	}

}