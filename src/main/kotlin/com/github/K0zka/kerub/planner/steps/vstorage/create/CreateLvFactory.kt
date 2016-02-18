package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.planner.OperationalState

object CreateLvFactory : AbstractVStorageCreateFactory<CreateLv>() {

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
				steps += storageNotAllocated.map {
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

}