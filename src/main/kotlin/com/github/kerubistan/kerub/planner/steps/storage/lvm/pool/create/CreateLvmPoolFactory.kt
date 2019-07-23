package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.common.percents
import com.github.kerubistan.kerub.utils.div
import io.github.kerubistan.kroki.collections.join
import io.github.kerubistan.kroki.size.GB
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.KClass

object CreateLvmPoolFactory : AbstractOperationalStepFactory<CreateLvmPool>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	private val minimumThinGroupSize = 16.GB

	override fun produce(state: OperationalState): List<CreateLvmPool> {
		return state.index.runningHosts.mapNotNull { hostData ->

			//all the pools on the host vgname -> pool
			val pools = hostData.config?.storageConfiguration
					?.filterIsInstance(LvmPoolConfiguration::class.java)?.associateBy { it.vgName } ?: mapOf()

			//all lvm volume groups where there is no pool
			hostData.stat.capabilities?.storageCapabilities?.filterIsInstance(LvmStorageCapability::class.java)
					?.filter { lvmCapability ->
						hostData.config?.storageConfiguration?.none {
							it is LvmPoolConfiguration && !pools.containsKey(lvmCapability.volumeGroupName)
						} ?: false
					}?.mapNotNull { lvmCapability ->
						val freeCapacity: BigInteger? = hostData.dynamic?.storageStatus
								?.singleOrNull { it.id == lvmCapability.id }?.freeCapacity
						if(freeCapacity != null
								&& freeCapacity > minimumThinGroupSize
								&& freeCapacity < lvmCapability.size) {
							Triple(lvmCapability, hostData, freeCapacity)
						} else {
							null
						}
					}

		}.join().map { (capability, hostData, freeCap) ->
			percents.map { percent ->
				CreateLvmPool(
						host = hostData.stat,
						name = UUID.randomUUID().toString(),
						size = (freeCap / 100).toBigInteger() * percent.toBigInteger(),
						vgName = capability.volumeGroupName
				)
			}
		}.join()
	}
}