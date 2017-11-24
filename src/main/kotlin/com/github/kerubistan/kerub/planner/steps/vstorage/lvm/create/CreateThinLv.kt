package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage

class CreateThinLv(
		override val host: Host,
		override val disk: VirtualStorageDevice,
		val volumeGroupName: String,
		val poolName: String) : AbstractCreateVirtualStorage {
	override fun take(state: OperationalState): OperationalState {
		TODO()
	}
}