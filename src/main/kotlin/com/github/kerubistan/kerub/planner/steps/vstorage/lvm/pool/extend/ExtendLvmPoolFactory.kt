package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.extend

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.StorageDeviceDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.join


/**
 * Generates extend LVM steps.
 */
object ExtendLvmPoolFactory : AbstractOperationalStepFactory<ExtendLvmPool>() {

	/*
	 * Questions regarding lvm pool extension
	 *
	 * - how much total space is requested by the virtual storage allocated on this pool?
	 * - is this smaller than the actual size of the pool?
	 * - how soon could this pool be filled up?
	 * - what amount of free space is available?
	 */
	override fun produce(state: OperationalState): List<ExtendLvmPool> =
			state.hosts.map { host ->
				host.value.config?.storageConfiguration?.filterIsInstance(LvmPoolConfiguration::class.java)?.map { pool ->
					val capability = host.value.stat.capabilities!!.storageCapabilities
							.filterIsInstance<LvmStorageCapability>().single {
						it.volumeGroupName == pool.vgName
					}
					host.value.dynamic?.storageStatus?.first { it.id == capability.id }?.let { poolStatus ->
						if (needExtend(pool, poolStatus, state) && canExtend(pool, poolStatus, state)) {
							ExtendLvmPool(
									host = host.value.stat,
									pool = pool.poolName,
									addSize = pool.size.divide(10.toBigInteger()),
									vgName = pool.vgName)
						} else {
							null
						}
					}

				}?.filterNotNull() ?: listOf()
			}.join()

	private fun canExtend(pool: LvmPoolConfiguration,
						  poolStatus: StorageDeviceDynamic,
						  state: OperationalState): Boolean {
		TODO()
	}

	private fun needExtend(pool: LvmPoolConfiguration,
						   poolStatus: StorageDeviceDynamic,
						   state: OperationalState): Boolean {
		TODO()
	}
}