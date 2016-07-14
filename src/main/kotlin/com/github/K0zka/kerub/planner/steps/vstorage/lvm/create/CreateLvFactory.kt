package com.github.K0zka.kerub.planner.steps.vstorage.lvm.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.model.StorageCapability
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorageFactory
import java.math.BigInteger

object CreateLvFactory : AbstractCreateVirtualStorageFactory<CreateLv>() {

	override fun produce(state: OperationalState): List<CreateLv> {

		val storageNotAllocated = listStorageNotAllocated(state)
		val runningHosts = listRunningHosts(state)

		var steps = listOf<CreateLv>()

		runningHosts.forEach {
			host ->
			host.capabilities?.storageCapabilities?.filter {
				capability
				->
				capability is LvmStorageCapability
			}?.forEach {
				volGroup ->
				steps += storageNotAllocated.filter {
					volGroup.size > it.size
							&& actualFreeCapacity(host, state, volGroup) > it.size
				}.map {
					disk ->
					CreateLv(
							host = host,
							volumeGroupName = (volGroup as LvmStorageCapability).volumeGroupName,
							disk = disk
					)
				}
			}
		}
		return steps
	}

	private fun actualFreeCapacity(host: Host, state: OperationalState, volGroup: StorageCapability)
			= state.hostDyns[host.id]?.storageStatus?.firstOrNull { it.id == volGroup.id }?.freeCapacity ?: BigInteger.ZERO

}