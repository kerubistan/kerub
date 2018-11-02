package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.extend

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.common.percents
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass


/**
 * Generates extend LVM steps.
 */
object ExtendLvmPoolFactory : AbstractOperationalStepFactory<ExtendLvmPool>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

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
						percents.map {
							ExtendLvmPool(
									host = host.value.stat,
									pool = pool.poolName,
									addSize = poolStatus.freeCapacity.divide(it.toBigInteger()),
									vgName = pool.vgName)
						}
					}

				}?.filterNotNull() ?: listOf()
			}.join().join()

}