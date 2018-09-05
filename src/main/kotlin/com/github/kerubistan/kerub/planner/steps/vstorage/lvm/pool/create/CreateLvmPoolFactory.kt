package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.create

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.common.percents
import com.github.kerubistan.kerub.utils.join
import java.math.BigInteger
import java.util.UUID

object CreateLvmPoolFactory : AbstractOperationalStepFactory<CreateLvmPool>() {

	override fun produce(state: OperationalState): List<CreateLvmPool> = state.runningHosts
			.map { hostData ->

				//all the pools on the host vgname -> pool
				val pools = hostData.config?.storageConfiguration
						?.filterIsInstance(LvmPoolConfiguration::class.java)?.associateBy { it.vgName } ?: mapOf()

				//all lvm volume groups where there is no pool
				hostData.stat.capabilities?.storageCapabilities?.filterIsInstance(LvmStorageCapability::class.java)
						?.filter { lvmCapability ->
							hostData.config?.storageConfiguration?.none {
								it is LvmPoolConfiguration && !pools.containsKey(it.vgName)
							} ?: false
						}?.map { lvmCapability ->
					val freeCapacity = hostData.dynamic?.storageStatus
							?.singleOrNull { it.id == lvmCapability.id }?.freeCapacity
					Triple(lvmCapability, hostData, freeCapacity ?: BigInteger.ZERO)
				}

			}.filterNotNull().join().map {
		percents.map { percent ->
			CreateLvmPool(
					host = it.second.stat,
					name = UUID.randomUUID().toString(),
					size = it.first.size / percent.toBigInteger(),
					vgName = it.first.volumeGroupName
			)
		}
	}.join()
}