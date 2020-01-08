package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.extend

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

class ExtendLvmPoolExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostConfigDao: HostConfigurationDao,
		private val hostDynDao: HostDynamicDao)
	: AbstractStepExecutor<ExtendLvmPool, Pair<BigInteger, BigInteger>>() {
	override fun update(step: ExtendLvmPool, updates: Pair<BigInteger, BigInteger>) {
		val capability = requireNotNull(step.host.capabilities) { "capabilities required for ${step.host}" }
				.storageCapabilities.single {
			it is LvmStorageCapability && it.volumeGroupName == step.vgName
		}
		hostConfigDao.update(step.host.id, change = {
			it.copy(
					storageConfiguration = it.storageConfiguration.map {
						if (it is LvmPoolConfiguration && it.poolName == step.pool && it.vgName == step.vgName) {
							it.copy(size = updates.second)
						} else it
					}
			)
		})
		hostDynDao.update(step.host.id, change = {
			it.copy(
					storageStatus = it.storageStatus.map {
						if (it.id == capability.id) {
							(it as SimpleStorageDeviceDynamic).copy(
									freeCapacity = updates.first
							)
						} else it
					}
			)
		})
	}

	override fun perform(step: ExtendLvmPool): Pair<BigInteger, BigInteger> =
			hostCommandExecutor.execute(step.host) {
				LvmLv.extend(it, step.vgName, step.pool, step.addSize)
				LvmVg.list(it, step.vgName).single().freeSize to LvmLv.list(it, step.vgName, step.pool).single().size
			}
}